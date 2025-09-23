package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class GenerateVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_video)

        // Hook up spinner (model selection)
        val spinnerModel: Spinner = findViewById(R.id.spinnerModelVideo)

        // Hook up button (safe for now, no crash)
        val btnGenerateVideo: Button = findViewById(R.id.btnVideo)
        btnGenerateVideo.setOnClickListener {
            // TODO: Add video generation logic
        }
    }
}
