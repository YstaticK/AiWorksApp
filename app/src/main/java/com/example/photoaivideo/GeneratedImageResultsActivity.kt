package com.example.photoaivideo

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar

class GeneratedImageResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)
        val recyclerView: androidx.recyclerview.widget.RecyclerView = findViewById(R.id.gridGeneratedImages)

        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)



        val imagesDir = getExternalFilesDir("generated_images")

        val images = imagesDir?.listFiles()?.toList() ?: emptyList()



        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest

        if (request != null) {

            recyclerView.adapter = GeneratedImageAdapter(this, images, request)

        }

        val tvTitle: TextView = findViewById(R.id.tvImageResultsTitle)
        val ivReferencePreview: ImageView = findViewById(R.id.ivReferencePreview)

        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)

            // Show progress bar
            progressBar.visibility = View.VISIBLE

            // Later, when generation finishes:
            progressBar.visibility = View.GONE

            // Show progress bar when generation starts
            progressBar.visibility = View.VISIBLE

            // TODO: Call this when generation is finished
            progressBar.visibility = View.GONE


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
