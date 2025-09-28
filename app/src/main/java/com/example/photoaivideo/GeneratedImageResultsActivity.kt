package com.example.photoaivideo

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GeneratedImageResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Collect images to display
        val images = mutableListOf<File>()

        // Check if a reference image was passed in
        intent.getStringExtra("referenceImageUri")?.let { uriString ->
            val uri = Uri.parse(uriString)
            // Save temp file? For now just show directly via adapter
            val previewList = listOf(uri)
            recyclerView.adapter = ReferencePreviewAdapter(previewList)
            return
        }

        recyclerView.adapter = GeneratedImageAdapter(images)
    }
}
