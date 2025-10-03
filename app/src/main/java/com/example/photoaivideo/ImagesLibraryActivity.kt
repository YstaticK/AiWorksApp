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

        val recycler = findViewById<RecyclerView>(R.id.recyclerViewLibrary)
        recycler.layoutManager = GridLayoutManager(this, 2)

        // Load generated images from our app folder
        val imagesDir = File(getExternalFilesDir("images"), "misc")
        val files = (imagesDir.listFiles()?.filter { it.isFile } ?: emptyList())
            .sortedByDescending { it.lastModified() }

        recycler.adapter = GeneratedImageAdapter(
            context = this,
            files = files,
            request = null,
            selectable = false
        )
    }
}
