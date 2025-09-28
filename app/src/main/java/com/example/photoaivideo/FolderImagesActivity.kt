package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderImagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        val folderPath = intent.getStringExtra("folderPath") ?: return
        val folder = File(folderPath)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val images = folder.listFiles()?.filter { it.isFile }?.toMutableList() ?: mutableListOf()

        // Use dummy request since library images don’t carry metadata
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

        recyclerView.adapter = GeneratedImageAdapter(this, images, dummyRequest)
    }
}
