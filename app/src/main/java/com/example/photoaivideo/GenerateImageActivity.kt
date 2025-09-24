package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        val btnStartGeneration: Button = findViewById(R.id.btnStartGeneration)
        btnStartGeneration.setOnClickListener {
            try {
                Toast.makeText(this, "Generation started!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
