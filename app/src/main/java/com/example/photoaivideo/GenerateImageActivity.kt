package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        // Hook up spinner (model selection)
        val spinnerModel: Spinner = findViewById(R.id.spinnerModelImage)

        // Hook up button (safe for now, no crash)
        val btnGenerateImage: Button = findViewById(R.id.btnImage)
        btnGenerateImage.setOnClickListener {
            // TODO: Add image generation logic
        }
    }
}
