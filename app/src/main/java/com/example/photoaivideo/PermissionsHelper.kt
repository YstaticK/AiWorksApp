package com.example.photoaivideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast

object PermissionsHelper {

    private const val REQUEST_CODE_STORAGE = 2001

    fun hasStoragePermission(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android.os.Environment.isExternalStorageManager()
        } else {
            val read = ContextCompat.checkSelfPermission(
                activity, android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val write = ContextCompat.checkSelfPermission(
                activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                Toast.makeText(activity, "Please allow access to manage all files for full functionality.", Toast.LENGTH_LONG).show()
                activity.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                Toast.makeText(activity, "Please allow access to manage all files for full functionality.", Toast.LENGTH_LONG).show()
                activity.startActivity(intent)
            }
        } else {
            Toast.makeText(activity, "Please grant storage permissions for full functionality.", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_STORAGE
            )
        }
    }
}
