package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class GeneratedImageResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        val title: TextView = findViewById(R.id.tvImageResultsTitle)
        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)

        // Placeholder data (replace with actual generated images later)
        val sampleImages = listOf(R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground)
        val sampleCaptions = listOf("Image 1", "Image 2")

        val adapter = GeneratedImageAdapter(sampleImages, sampleCaptions)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }
}
