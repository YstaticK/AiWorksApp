package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GenerateVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_video)

        val generateButton: Button = findViewById(R.id.btnGenerateVideo)
        generateButton.setOnClickListener {
            // Navigate to video results screen (placeholder for now)
            val intent = Intent(this, GeneratedVideoResultsActivity::class.java)
            startActivity(intent)
        }
    }
}
