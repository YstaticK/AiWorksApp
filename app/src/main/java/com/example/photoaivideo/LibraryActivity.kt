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
            val intent = Intent(this, ImagesLibraryActivity::class.java)
            startActivity(intent)
        }

        btnVideos.setOnClickListener {
            val intent = Intent(this, VideosLibraryActivity::class.java)
            startActivity(intent)
        }
    }
}
