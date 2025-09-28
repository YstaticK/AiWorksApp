package com.example.photoaivideo

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GeneratedImageResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        val tvTitle: TextView = findViewById(R.id.tvImageResultsTitle)
        val ivReferencePreview: ImageView = findViewById(R.id.ivReferencePreview)

        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest

        if (request != null) {
            tvTitle.text = """
                Prompts: ${request.prompts}
                Negative: ${request.negativePrompt}
                Similarity: ${request.similarity}%
                Size: ${request.width}x${request.height}
                Quality: ${request.quality}
                Batch: ${request.batchSize}
                Seed: ${request.seed ?: "Auto"}
            """.trimIndent()

            if (request.referenceImageUri != null) {
                ivReferencePreview.setImageURI(Uri.parse(request.referenceImageUri))
                ivReferencePreview.visibility = ImageView.VISIBLE
            }
        } else {
            tvTitle.text = "No request data received."
        }
    }
}
