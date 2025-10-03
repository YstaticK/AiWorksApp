package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ReferenceLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_library)

        val btnImages = findViewById<Button>(R.id.btnReferenceImages)
        btnImages.setOnClickListener {
            startActivity(Intent(this, ReferenceImagesActivity::class.java))
        }

        // Videos and URLs can remain placeholders for now
    }
}
