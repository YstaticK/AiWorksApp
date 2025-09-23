package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GenerateVideoActivity : AppCompatActivity() {

    private lateinit var spinnerModelVideo: Spinner
    private lateinit var btnGenerateVideo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_video)

        spinnerModelVideo = findViewById(R.id.spinnerModelVideo)
        btnGenerateVideo = findViewById(R.id.btnVideo)

        // Example spinner values
        val models = listOf("Video Model 1", "Video Model 2")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, models)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModelVideo.adapter = adapter

        btnGenerateVideo.setOnClickListener {
            val intent = Intent(this, GeneratedVideoResultsActivity::class.java)
            startActivity(intent)
        }
    }
}
