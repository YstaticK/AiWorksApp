package com.example.photoaivideo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderImagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_images)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerFolderImages)
        val tvEmpty: TextView = findViewById(R.id.tvEmptyFolder)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        try {
            val folderPath = intent.getStringExtra("folderPath")
            if (folderPath.isNullOrBlank()) {
                ErrorUtils.showErrorDialog(
                    this,
                    "Could not open folder: missing path.\n\nTip: please try re-opening the library."
                )
                return
            }

            val folder = File(folderPath)
            if (!folder.exists() || !folder.isDirectory) {
                ErrorUtils.showErrorDialog(
                    this,
                    "Could not open folder: path is invalid.\n\nPath: $folderPath"
                )
                return
            }

            val images = folder.listFiles { file ->
                file.isFile && (
                    file.extension.equals("png", true) ||
                    file.extension.equals("jpg", true) ||
                    file.extension.equals("jpeg", true) ||
                    file.extension.equals("webp", true)
                )
            }?.toList() ?: emptyList()

            if (images.isNotEmpty()) {
                recyclerView.adapter = FolderImagesAdapter(this, images)
                tvEmpty.visibility = TextView.GONE
            } else {
                tvEmpty.visibility = TextView.VISIBLE
            }
        } catch (e: Exception) {
            ErrorUtils.showErrorDialog(
                this,
                "Failed to load folder.\n\n${e::class.java.simpleName}: ${e.message}"
            )
        }
    }
}
