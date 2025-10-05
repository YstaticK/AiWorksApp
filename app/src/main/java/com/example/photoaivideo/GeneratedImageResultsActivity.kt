package com.example.photoaivideo

import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class GeneratedImageResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Generation Result"

        // Create a simple vertical layout to show parameters (for now)
        val scrollView = ScrollView(this)
        val container = TextView(this)
        container.setPadding(24)
        scrollView.addView(container)
        setContentView(scrollView)

        // Get the request object
        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest

        if (request == null) {
            container.text = "No generation request data found."
            return
        }

        // Display generation parameters for testing
        val info = """
            🧠 Model: ${request.model}
            💬 Prompt: ${request.prompts}
            🚫 Negative: ${request.negativePrompt}
            ⚙️ Sampler: ${request.samplingMethod}
            🔢 Steps: ${request.samplingSteps}
            🎚 CFG Scale: ${request.cfgScale}
            📐 Width: ${request.width}
            📏 Height: ${request.height}
            🧩 Batch Count: ${request.batchCount}
            🧩 Batch Size: ${request.batchSize}
            🌱 Seed: ${request.seed}
            🪄 Hires Fix: ${request.hiresFix}
            🧬 Refiner: ${request.refiner}
            🎨 LoRA: ${request.lora}
            🖼 Reference Image: ${if (request.referenceImageUri.isNotEmpty()) request.referenceImageUri else "None"}
            🤝 Similarity: ${request.similarity}
            📊 Quality: ${request.quality}
        """.trimIndent()

        container.text = info

        // Placeholder for generated image (will display later when backend is added)
        val imagePreview = ImageView(this)
        imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
    }
}
