package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ReferenceLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_library)

        val btnImages = findViewById<Button>(R.id.btnReferenceImages)
        val btnVideos = findViewById<Button>(R.id.btnReferenceVideos)
        val btnUrls = findViewById<Button>(R.id.btnReferenceUrls)

        btnImages.setOnClickListener {
            startActivity(Intent(this, ReferenceImagesActivity::class.java))
        }

        btnVideos.setOnClickListener {
            startActivity(Intent(this, ReferenceVideosActivity::class.java))
        }

        btnUrls.setOnClickListener {
            startActivity(Intent(this, ReferenceUrlsActivity::class.java))
        }
    }
}
