package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GeneratedImageResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        val recyclerView = findViewById<RecyclerView>(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val generatedDir = File(filesDir, "generated_images")
        val imageFiles = generatedDir.listFiles()?.toList() ?: emptyList()

        recyclerView.adapter = GeneratedImageAdapter(imageFiles)
    }
}
