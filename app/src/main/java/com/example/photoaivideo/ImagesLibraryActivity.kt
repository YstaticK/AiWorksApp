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

        // Setup RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Load images from misc
        val miscDir = File(getExternalFilesDir("images"), "misc")
        if (!miscDir.exists()) miscDir.mkdirs()
        val images = miscDir.listFiles()?.toMutableList() ?: mutableListOf()

        // Dummy request (since library images don’t carry request metadata)
        val dummyRequest = GenerationRequest(
            provider = "N/A",
            model = "N/A",
            prompts = "",
            negativePrompt = "",
            similarity = 0,
            seed = null,
            width = 0,
            height = 0,
            quality = "",
            batchSize = 1,
            referenceImageUri = null
        )

        val adapter = GeneratedImageAdapter(this, images, dummyRequest)
        recyclerView.adapter = adapter
    }
}
