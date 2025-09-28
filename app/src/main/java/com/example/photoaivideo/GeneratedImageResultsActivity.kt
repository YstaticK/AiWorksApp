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

class GeneratedImageResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        // Progress bar
        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)
        progressBar.max = 100
        progressBar.visibility = View.VISIBLE

        // Recycler
        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Get request + API key
        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", null)

        if (request == null) {
            Toast.makeText(this, "No generation request found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (apiKey.isNullOrBlank()) {
            Toast.makeText(this, "Missing API key. Please enter it on the previous screen.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Kick a small fake animation while we wait (optional polish)
        Thread {
            for (i in 1..100) {
                Thread.sleep(40)
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

        // Call the service
        val service = OpenAIService(this, apiKey)
        service.generateImage(
            prompt = request.prompts,
            width = request.width,
            height = request.height,
            n = request.batchSize
        ) { files: List<java.io.File>? ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (files != null && files.isNotEmpty()) {
                    recyclerView.adapter = GeneratedImageAdapter(this, files, request)
                } else {
                    Toast.makeText(this, "Image generation failed.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
