package com.example.photoaivideo

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FullscreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val imageView: ImageView = findViewById(R.id.ivFullscreen)
        val uriString = intent.getStringExtra("imageUri")

        if (!uriString.isNullOrEmpty()) {
            val uri = Uri.parse(uriString)
            imageView.setImageURI(uri)
        }
    }
}
