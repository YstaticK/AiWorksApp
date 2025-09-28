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

        // Load request if available
        val apiKey = intent.getStringExtra("apiKey") ?: ""

        if (apiKey.isEmpty()) {

            Toast.makeText(this, "API key missing!", Toast.LENGTH_LONG).show()

            finish()

            return

        }
        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        if (request != null) {
            val service = OpenAIService(this)
            service.generateImage(
                prompt = request.prompts,
                width = request.width,
                height = request.height
            ) { files ->
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    if (files != null && files.isNotEmpty()) {
                        recyclerView.adapter = GeneratedImageAdapter(this, files.toMutableList(), request)
                    }
                }
            }
        }
    }
}
