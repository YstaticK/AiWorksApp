package com.example.photoaivideo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.photoaivideo.models.GenerationRequest


class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView: ImageView = findViewById(R.id.ivFullScreen)
        val path = intent.getStringExtra("imagePath")
        if (path != null) {
            val bitmap = BitmapFactory.decodeFile(path)
            imageView.setImageBitmap(bitmap)
        }

        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        request?.let {
            val details = listOf(
                "Prompt: ${it.prompts}",
                "Negative Prompt: ${it.negativePrompt ?: "N/A"}",
                "Similarity: ${it.similarity}",
                "Size: ${it.width}x${it.height}",
                "Quality: ${it.quality}",
                "Batch Size: ${it.batchSize}",
                "Seed: ${it.seed ?: "Random"}"
            )

            val spinner: Spinner = findViewById(R.id.spinnerGenerationDetails)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, details)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }
}
