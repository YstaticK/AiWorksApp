package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class LibraryActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }

    override fun onStoragePermissionGranted() {
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
