package com.example.photoaivideo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView = findViewById<ImageView>(R.id.ivFullScreenImage)
        val path = intent.getStringExtra("imagePath")

        if (path != null) {
            val bitmap = BitmapFactory.decodeFile(path)
            imageView.setImageBitmap(bitmap)
        }
    }
}
