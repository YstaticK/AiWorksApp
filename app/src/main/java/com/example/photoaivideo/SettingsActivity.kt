package com.example.photoaivideo

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private val necessaryPermissions = listOf(
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE
    )

    private val optionalPermissions = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.READ_MEDIA_VIDEO,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val container = findViewById<LinearLayout>(R.id.permissionsContainer)

        // Add headers
        val necessaryHeader = TextView(this).apply {
            text = "Necessary Permissions"
            textSize = 18f
            setPadding(0, 20, 0, 10)
        }
        container.addView(necessaryHeader)
        addPermissionSwitches(container, necessaryPermissions)

        val optionalHeader = TextView(this).apply {
            text = "Optional Permissions"
            textSize = 18f
            setPadding(0, 30, 0, 10)
        }
        container.addView(optionalHeader)
        addPermissionSwitches(container, optionalPermissions)
    }

    private fun addPermissionSwitches(container: LinearLayout, permissions: List<String>) {
        for (perm in permissions) {
            val switch = androidx.appcompat.widget.SwitchCompat(this).apply {
                text = perm
                isChecked = ContextCompat.checkSelfPermission(
                    this@SettingsActivity,
                    perm
                ) == PackageManager.PERMISSION_GRANTED

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        ActivityCompat.requestPermissions(
                            this@SettingsActivity,
                            arrayOf(perm),
                            1001
                        )
                    } else {
                        // Can't revoke programmatically, just reset state
                        this.isChecked = ContextCompat.checkSelfPermission(
                            this@SettingsActivity,
                            perm
                        ) == PackageManager.PERMISSION_GRANTED
                    }
                }
            }
            container.addView(switch)
        }
    }
}
