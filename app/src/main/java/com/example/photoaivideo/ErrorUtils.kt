package com.example.photoaivideo

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object ErrorUtils {

    /**
     * Show a detailed error dialog with copy support.
     * Works in any Activity.
     */
    fun showErrorDialog(activity: Activity, message: String) {
        activity.runOnUiThread {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Error")
            builder.setMessage(message)
            builder.setPositiveButton("OK", null)
            builder.setNeutralButton("Copy") { _, _ ->
                val clipboard =
                    activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Error Message", message)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(activity, "Error copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }
    }
}
