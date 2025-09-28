package com.example.photoaivideo

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.graphics.PorterDuff
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GeneratedImageResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        // Progress bar setup
        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)
        progressBar.max = 100
        progressBar.visibility = View.VISIBLE

        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        val apiKey = intent.getStringExtra("apiKey")

        if (request == null || apiKey.isNullOrEmpty()) {
            Toast.makeText(this, "Missing request or API key", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Prepare placeholder files (null values mean not yet generated)
        val placeholderList = MutableList<File?>(request.batchSize) { null }
        val adapter = GeneratedImageAdapter(this, placeholderList.mapNotNull { it }, request)
        recyclerView.adapter = adapter

        // Call OpenAI service to generate images
        val service = OpenAIService(this, apiKey)
        service.generateImage(
            prompt = request.prompts,
            width = request.width,
            height = request.height
        ) { files ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (files != null && files.isNotEmpty()) {
                    adapter.updateData(files) // refresh with generated files
                    progressBar.progressDrawable.setColorFilter(
                        ContextCompat.getColor(this, android.R.color.holo_green_light),
                        PorterDuff.Mode.SRC_IN
                    )
                } else {
                    Toast.makeText(this, "Image generation failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
