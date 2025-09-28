package com.example.photoaivideo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class GeneratedImageResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)
        if (request != null) {
            // TODO: use request.prompts, request.model, etc.
        }

        val tvImageResultsTitle: TextView = findViewById(R.id.tvImageResultsTitle)
        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)

        // Get request from intent
        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest

        if (request != null) {
            tvImageResultsTitle.text = """
                Prompt: ${request.prompt}
                Negative: ${request.negativePrompt ?: "none"}
                Similarity: ${request.similarity}%
                Seed: ${request.seed ?: "random"}
                Size: ${request.width}x${request.height}
                Quality: ${request.quality}
                Batch: ${request.batchSize}
                Reference: ${request.referenceImageUri ?: "none"}
            """.trimIndent()
        } else {
            tvImageResultsTitle.text = "No generation request received."
        }

        // TODO: Later - load real generated images into recyclerView
    }
}
