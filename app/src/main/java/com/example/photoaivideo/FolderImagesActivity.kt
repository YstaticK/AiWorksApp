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

        val folderPath = intent.getStringExtra("folderPath")
        val folder = File(folderPath ?: "")

        if (folder.exists() && folder.isDirectory) {
            val images = folder.listFiles { file ->
                file.isFile && (file.extension.equals("png", true)
                        || file.extension.equals("jpg", true)
                        || file.extension.equals("jpeg", true))
            }?.toList() ?: emptyList()

            if (images.isNotEmpty()) {
                recyclerView.adapter = FolderImagesAdapter(this, images)
                tvEmpty.visibility = TextView.GONE
            } else {
                tvEmpty.visibility = TextView.VISIBLE
            }
        } else {
            tvEmpty.visibility = TextView.VISIBLE
        }
    }
}
