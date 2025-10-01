package com.example.photoaivideo

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchStorage: Switch
    private lateinit var switchCamera: Switch
    private lateinit var switchNotifications: Switch

    private val prefs by lazy { getSharedPreferences("permissions_prefs", Context.MODE_PRIVATE) }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (perm, granted) ->
            val status = if (granted) "granted" else "denied"
            Toast.makeText(this, "$perm: $status", Toast.LENGTH_SHORT).show()
        }
        updateSwitchStates()
        saveSwitchStates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchStorage = findViewById(R.id.switchStorage)
        switchCamera = findViewById(R.id.switchCamera)
        switchNotifications = findViewById(R.id.switchNotifications)

        // Restore saved states first
        restoreSwitchStates()

        // Then sync with actual Android permission status
        updateSwitchStates()

        switchStorage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) requestPermissionsGroup("storage")
            saveSwitchStates()
        }

        switchCamera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) requestPermissionsGroup("camera")
            saveSwitchStates()
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) requestPermissionsGroup("notifications")
            saveSwitchStates()
        }
    }

    private fun requestPermissionsGroup(group: String) {
        when (group) {
            "storage" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(arrayOf(
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_VIDEO
                    ))
                } else {
                    permissionLauncher.launch(arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                }
            }
            "camera" -> {
                permissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA))
            }
            "notifications" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
                } else {
                    Toast.makeText(this, "Notifications don't require permission on this Android version", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateSwitchStates() {
        switchStorage.isChecked = hasStoragePermission()
        switchCamera.isChecked =
            checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        switchNotifications.isChecked =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
        } else {
            checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun saveSwitchStates() {
        prefs.edit()
            .putBoolean("storage", switchStorage.isChecked)
            .putBoolean("camera", switchCamera.isChecked)
            .putBoolean("notifications", switchNotifications.isChecked)
            .apply()
    }

    private fun restoreSwitchStates() {
        switchStorage.isChecked = prefs.getBoolean("storage", false)
        switchCamera.isChecked = prefs.getBoolean("camera", false)
        switchNotifications.isChecked = prefs.getBoolean("notifications", false)
    }
}
