package com.example.photoaivideo

import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GeneratedImageResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)
        progressBar.max = 100
        progressBar.visibility = View.VISIBLE

        Thread {
            for (i in 1..100) {
                Thread.sleep(50)
                runOnUiThread {
                    progressBar.progress = i
                    if (i == 100) {
                        progressBar.progressDrawable.setColorFilter(
                            ContextCompat.getColor(this, android.R.color.holo_green_light),
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }
        }.start()

        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val imagesDir = getExternalFilesDir("generated_images")
        val images = imagesDir?.listFiles()?.toList() ?: emptyList()

        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest

        val tvTitle: TextView = findViewById(R.id.tvImageResultsTitle)
        val ivReferencePreview: ImageView = findViewById(R.id.ivReferencePreview)

        if (request != null) {
            recyclerView.adapter = GeneratedImageAdapter(this, images, request)
            tvTitle.text = "Seed: ${request.seed ?: "Auto"}"

            if (request.referenceImageUri != null) {
                ivReferencePreview.setImageURI(Uri.parse(request.referenceImageUri))
                ivReferencePreview.visibility = ImageView.VISIBLE
            }
        } else {
            tvTitle.text = "No request data received."
        }
    }
}
