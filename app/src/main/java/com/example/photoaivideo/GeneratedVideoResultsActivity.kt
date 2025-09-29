package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GeneratedVideoResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_generated_video_results)
        } catch (e: Exception) {
            ErrorUtils.showErrorDialog(this, "Error loading GeneratedVideoResultsActivity:\n${e.message}")
        }
    }
}
