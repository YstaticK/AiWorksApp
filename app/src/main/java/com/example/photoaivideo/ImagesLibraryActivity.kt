package com.example.photoaivideo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GeneratedImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        recyclerView = findViewById(R.id.recyclerViewLibrary)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        loadImages()
    }

    private fun loadImages() {
        val saveDir = File(getExternalFilesDir("images"), "misc")

        if (!saveDir.exists() || saveDir.listFiles().isNullOrEmpty()) {
            Toast.makeText(this, "No images in library yet", Toast.LENGTH_SHORT).show()
            return
        }

        val files = saveDir.listFiles()
            ?.filter { it.isFile && (it.name.endsWith(".png") || it.name.endsWith(".jpg")) }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()

        if (files.isEmpty()) {
            Toast.makeText(this, "No images in library yet", Toast.LENGTH_SHORT).show()
            return
        }

        adapter = GeneratedImageAdapter(this, files, null)
        recyclerView.adapter = adapter
    }
}
