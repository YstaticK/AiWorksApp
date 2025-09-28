package com.example.photoaivideo

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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

        // RecyclerView setup
        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load generated images
        val imagesDir = getExternalFilesDir("generated_images")
        val images = imagesDir?.listFiles()?.toList() ?: emptyList()

        // Load request if available
        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        if (request != null) {
            recyclerView.adapter = GeneratedImageAdapter(this, images, request)
        }
    }
}
