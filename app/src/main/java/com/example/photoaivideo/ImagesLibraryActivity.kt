package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        // Ensure misc folder exists
        val miscDir = File(getExternalFilesDir("images"), "misc")
        if (!miscDir.exists()) {
            miscDir.mkdirs()
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Load images from misc
        val images = miscDir.listFiles()?.toList() ?: emptyList()
        val adapter = GeneratedImageAdapter(this, images, null)
        recyclerView.adapter = adapter
    }
}
