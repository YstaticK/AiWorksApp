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

        // Use placeholder drawable to avoid missing resource errors
        val sampleImages = listOf(
            R.drawable.placeholder,
            R.drawable.placeholder,
            R.drawable.placeholder
        )
        val sampleCaptions = listOf("Preview 1", "Preview 2", "Preview 3")

        val adapter = GeneratedImageAdapter(sampleImages, sampleCaptions)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }
}
