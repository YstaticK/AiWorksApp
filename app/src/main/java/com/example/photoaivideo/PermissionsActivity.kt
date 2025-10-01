package com.example.photoaivideo

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.SwitchCompat

class PermissionsActivity : AppCompatActivity() {

    private val necessaryPermissions: List<String> by lazy {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO
            )
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> listOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            else -> listOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private val optionalPermissions = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    // Map Android permission constants to human-readable labels
    private val permissionLabels = mapOf(
        android.Manifest.permission.READ_MEDIA_IMAGES to "Read Media Images",
        android.Manifest.permission.READ_MEDIA_VIDEO to "Read Media Videos",
        android.Manifest.permission.READ_EXTERNAL_STORAGE to "Read Storage (Legacy)",
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE to "Write Storage (Legacy)",
        android.Manifest.permission.CAMERA to "Camera",
        android.Manifest.permission.RECORD_AUDIO to "Microphone",
        android.Manifest.permission.POST_NOTIFICATIONS to "Notifications"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        val container = findViewById<LinearLayout>(R.id.permissionsContainer)

        // Necessary section
        val necessaryHeader = TextView(this).apply {
            text = "Necessary Permissions"
            textSize = 18f
            setPadding(0, 20, 0, 10)
        }
        container.addView(necessaryHeader)
        addPermissionSwitches(container, necessaryPermissions)

        // Optional section
        val optionalHeader = TextView(this).apply {
            text = "Optional Permissions"
            textSize = 18f
            setPadding(0, 30, 0, 10)
        }
        container.addView(optionalHeader)
        addPermissionSwitches(container, optionalPermissions)

        // Always on section
        val alwaysHeader = TextView(this).apply {
            text = "Always Enabled"
            textSize = 18f
            setPadding(0, 30, 0, 10)
        }
        container.addView(alwaysHeader)

        val alwaysOn = listOf(
            "Internet (always required)",
            "Network State (always required)"
        )
        alwaysOn.forEach { label ->
            val switch = SwitchCompat(this).apply {
                text = label
                isChecked = true
                isEnabled = false
            }
            container.addView(switch)
        }
    }

    private fun addPermissionSwitches(container: LinearLayout, permissions: List<String>) {
        for (perm in permissions) {
            val label = permissionLabels[perm] ?: perm // fallback if missing
            val switch = SwitchCompat(this).apply {
                text = label
                isChecked = ContextCompat.checkSelfPermission(
                    this@PermissionsActivity,
                    perm
                ) == PackageManager.PERMISSION_GRANTED

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        ActivityCompat.requestPermissions(
                            this@PermissionsActivity,
                            arrayOf(perm),
                            1001
                        )
                    } else {
                        // Can't revoke programmatically, reset to current state
                        this.isChecked = ContextCompat.checkSelfPermission(
                            this@PermissionsActivity,
                            perm
                        ) == PackageManager.PERMISSION_GRANTED
                    }
                }
            }
            container.addView(switch)
        }
    }
}
