package com.example.photoaivideo

import android.view.View
import android.widget.ProgressBar
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
        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)
        progressBar.visibility = View.VISIBLE

        // TODO: hide it once generation completes

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
