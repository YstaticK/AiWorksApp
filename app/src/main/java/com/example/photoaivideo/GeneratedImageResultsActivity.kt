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

        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)
        progressBar.max = 100
        progressBar.visibility = View.VISIBLE

        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load request
        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        val service = OpenAIService(this)

        if (request != null) {
            service.generateImage(
                prompt = request.prompts,
                width = request.width,
                height = request.height
            ) { files ->
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    if (files != null && files.isNotEmpty()) {
                        recyclerView.adapter = GeneratedImageAdapter(this, files, request)
                    }
                }
            }
        }
    }
}
