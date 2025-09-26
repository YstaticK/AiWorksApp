package com.example.photoaivideo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class VideosLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos_library)

        // Always ensure misc. Videos exists
        ensureDefaultFolder("VideosLibrary/misc. Videos")

        // Show dialog immediately to create new video folders
        showCreateFolderDialog("VideosLibrary", "Video")
    }

    private fun ensureDefaultFolder(path: String) {
        val folder = File(getExternalFilesDir(null), path)
        if (!folder.exists()) {
            folder.mkdirs()
            Toast.makeText(this, "Created: ${folder.absolutePath}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCreateFolderDialog(base: String, type: String) {
        val input = android.widget.EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Create New $type Folder")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val newFolder = File(getExternalFilesDir(null), "$base/$name")
                    if (!newFolder.exists()) {
                        newFolder.mkdirs()
                        Toast.makeText(this, "Created: ${newFolder.absolutePath}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Folder already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
