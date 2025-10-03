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
        val btnLibrary: Button = findViewById(R.id.btnLibrary)
        val btnReferenceLibrary: Button = findViewById(R.id.btnReferenceLibrary)
        val btnModels: Button = findViewById(R.id.btnModels)
        val btnRecycleBin: Button = findViewById(R.id.btnRecycleBin)
        val btnSettings: Button = findViewById(R.id.btnSettings)
        val btnInfo: Button = findViewById(R.id.btnInfo)
        val btnQuit: Button = findViewById(R.id.btnQuit)

        btnImage.setOnClickListener {
            startActivity(Intent(this, GenerateImageActivity::class.java))
        }

        btnVideo.setOnClickListener {
            startActivity(Intent(this, VideosLibraryActivity::class.java))
        }

        btnLibrary.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        btnReferenceLibrary.setOnClickListener {
            startActivity(Intent(this, ReferenceLibraryActivity::class.java))
        }

        btnModels.setOnClickListener {
            startActivity(Intent(this, ModelsActivity::class.java))
        }

        btnRecycleBin.setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnInfo.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        btnQuit.setOnClickListener {
            finishAffinity()
        }
    }
}
