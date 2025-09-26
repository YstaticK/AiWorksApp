package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val btnImages = findViewById<Button>(R.id.btnImages)
        val btnVideos = findViewById<Button>(R.id.btnVideos)

        btnImages.setOnClickListener {
            startActivity(Intent(this, ImagesActivity::class.java))
        }

        btnVideos.setOnClickListener {
            startActivity(Intent(this, VideosActivity::class.java))
        }
    }
}
