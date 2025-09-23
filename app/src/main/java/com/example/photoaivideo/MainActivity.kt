package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnImage: Button = findViewById(R.id.btnImage)
        val btnVideo: Button = findViewById(R.id.btnVideo)

        btnImage.setOnClickListener {
            val intent = Intent(this, GenerateImageActivity::class.java)
            startActivity(intent)
        }

        btnVideo.setOnClickListener {
            val intent = Intent(this, GenerateVideoActivity::class.java)
            startActivity(intent)
        }
    }
}
