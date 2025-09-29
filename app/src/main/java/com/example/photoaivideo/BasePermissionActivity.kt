package com.example.photoaivideo

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

abstract class BasePermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    private fun checkPermission() {
        if (!PermissionsHelper.hasStoragePermission(this)) {
            PermissionsHelper.requestStoragePermission(this)
        } else {
            onStoragePermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionsHelper.REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onStoragePermissionGranted()
            } else {
                Toast.makeText(
                    this,
                    "Storage permission denied. This feature won’t work without it.",
                    Toast.LENGTH_LONG
                ).show()
                onStoragePermissionDenied()
            }
        }
    }

    // Child activities must implement granted case
    abstract fun onStoragePermissionGranted()

    // Optional override for denied case
    open fun onStoragePermissionDenied() {
        // Default does nothing
    }

    // --- Simple error logging helper ---
    fun logErrorToFile(message: String, throwable: Throwable? = null) {
        try {
            val dir = File(getExternalFilesDir("images"), "misc")
            if (!dir.exists()) dir.mkdirs()
            val logFile = File(dir, "error_log.txt")
            logFile.appendText(
                "\n[${System.currentTimeMillis()}] $message\n${throwable?.stackTraceToString() ?: ""}\n"
            )
        } catch (_: Exception) {
            // ignore logging failures
        }
    }
}
