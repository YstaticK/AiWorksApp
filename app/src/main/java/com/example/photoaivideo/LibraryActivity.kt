package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val btnImagesLibrary = findViewById<Button>(R.id.btnImagesLibrary)
        val btnVideosLibrary = findViewById<Button>(R.id.btnVideosLibrary)

        btnImagesLibrary.setOnClickListener {
            startActivity(Intent(this, ImagesLibraryActivity::class.java))
        }

        btnVideosLibrary.setOnClickListener {
            startActivity(Intent(this, VideosLibraryActivity::class.java))
        }
    }
}
