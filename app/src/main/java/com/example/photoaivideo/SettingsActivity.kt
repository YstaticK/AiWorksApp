package com.example.photoaivideo

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    // Always on permissions
    private val alwaysOnPermissions = mapOf(
        android.Manifest.permission.INTERNET to "Internet Access",
        android.Manifest.permission.ACCESS_NETWORK_STATE to "Check Network State"
    )

    // Necessary permissions
    private val necessaryPermissions = mapOf(
        android.Manifest.permission.READ_MEDIA_IMAGES to "Read Images",
        android.Manifest.permission.READ_MEDIA_VIDEO to "Read Videos",
        android.Manifest.permission.READ_EXTERNAL_STORAGE to "Read Storage (Legacy)",
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE to "Write Storage (Legacy)"
    )

    // Optional permissions
    private val optionalPermissions = mapOf(
        android.Manifest.permission.CAMERA to "Use Camera",
        android.Manifest.permission.RECORD_AUDIO to "Record Audio",
        android.Manifest.permission.POST_NOTIFICATIONS to "Show Notifications"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val container = findViewById<LinearLayout>(R.id.permissionsContainer)

        // Add styled sections
        addSectionHeader(container, "🔒 Always On")
        addPermissionSwitches(container, alwaysOnPermissions, alwaysOn = true)

        addSectionHeader(container, "⚠️ Necessary Permissions")
        addPermissionSwitches(container, necessaryPermissions, alwaysOn = false)

        addSectionHeader(container, "✨ Optional Permissions")
        addPermissionSwitches(container, optionalPermissions, alwaysOn = false)
    }

    private fun addSectionHeader(container: LinearLayout, title: String) {
        val header = TextView(this).apply {
            text = title
            textSize = 20f
            setTextColor(getColor(android.R.color.holo_blue_dark))
            setPadding(0, 40, 0, 20)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        container.addView(header)
    }

    private fun addPermissionSwitches(
        container: LinearLayout,
        permissions: Map<String, String>,
        alwaysOn: Boolean
    ) {
        for ((perm, label) in permissions) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20, 20, 20, 20)
                setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 10)
                layoutParams = params
            }

            val switch = SwitchCompat(this).apply {
                text = label
                textSize = 16f
                isChecked = ContextCompat.checkSelfPermission(
                    this@SettingsActivity, perm
                ) == PackageManager.PERMISSION_GRANTED
                isEnabled = !alwaysOn
            }

            val statusView = TextView(this).apply {
                textSize = 13f
                setTextColor(getColor(android.R.color.darker_gray))
                text = when {
                    alwaysOn -> "🔒 Always On"
                    switch.isChecked -> "✅ Granted"
                    else -> "❌ Not Granted"
                }
                setPadding(4, 6, 4, 0)
            }

            if (!alwaysOn) {
                switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        ActivityCompat.requestPermissions(
                            this@SettingsActivity,
                            arrayOf(perm),
                            1001
                        )
                    }
                    // Update immediately after interaction
                    val granted = ContextCompat.checkSelfPermission(
                        this@SettingsActivity, perm
                    ) == PackageManager.PERMISSION_GRANTED
                    statusView.text = if (granted) "✅ Granted" else "❌ Not Granted"
                }
            }

            row.addView(switch)
            row.addView(statusView)
            container.addView(row)
        }
    }

    // Refresh status if user responds to system permission dialog
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            recreate() // Quick refresh of UI
        }
    }
}
