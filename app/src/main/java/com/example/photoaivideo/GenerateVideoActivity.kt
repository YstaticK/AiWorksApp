package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class GenerateVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_video)

        try {
            val spinnerModel: Spinner = findViewById(R.id.spinnerModelVideo)
            val btnGenerateVideo: Button = findViewById(R.id.btnVideo)

            btnGenerateVideo.setOnClickListener {
                // Placeholder for actual generation logic
                ErrorUtils.showErrorDialog(this, "Video generation is not yet implemented.")
            }
        } catch (e: Exception) {
            ErrorUtils.showErrorDialog(this, "Error initializing GenerateVideoActivity:\n${e.message}")
        }
    }
}
