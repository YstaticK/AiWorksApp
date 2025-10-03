package com.example.photoaivideo

import android.content.Context
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileUtils {

    fun saveToReference(file: File, context: Context): Boolean {
        return try {
            val refDir = File(context.getExternalFilesDir("images"), "reference_images")
            if (!refDir.exists()) refDir.mkdirs()

            val destFile = File(refDir, file.name)
            copyFile(file, destFile)

            Toast.makeText(context, "Saved to Reference Library", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun moveToRecycleBin(file: File, context: Context): Boolean {
        return try {
            val recycleDir = File(context.getExternalFilesDir("images"), "recycle_bin")
            if (!recycleDir.exists()) recycleDir.mkdirs()

            val destFile = File(recycleDir, file.name)
            copyFile(file, destFile)

            // Delete original
            file.delete()

            Toast.makeText(context, "Moved to Recycle Bin", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to move: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun copyFile(src: File, dest: File) {
        FileInputStream(src).use { input ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        }
    }
}
