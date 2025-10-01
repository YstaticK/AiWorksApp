package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Reference Library button
        val btnReferenceLibrary = findViewById<Button>(R.id.btnReferenceLibrary)
        btnReferenceLibrary.setOnClickListener {
            startActivity(Intent(this, ReferenceLibraryActivity::class.java))
        }

        // Recycle Bin button
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)
        btnRecycleBin.setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
        }

        // Models button
        val btnModels = findViewById<Button>(R.id.btnModels)
        btnModels.setOnClickListener {
            startActivity(Intent(this, ModelsActivity::class.java))
        }

        // Library button
        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        btnLibrary.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        // Image generation button
        val btnImage: Button = findViewById(R.id.btnImage)
        btnImage.setOnClickListener {
            val intent = Intent(this, GenerateImageActivity::class.java)
            startActivity(intent)
        }

        // Video generation button
        val btnVideo: Button = findViewById(R.id.btnVideo)
        btnVideo.setOnClickListener {
            val intent = Intent(this, GenerateVideoActivity::class.java)
            startActivity(intent)
        }

        // Settings button
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Info button
        val btnInfo = findViewById<Button>(R.id.btnInfo)
        btnInfo.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        // Quit button
        val btnQuit = findViewById<Button>(R.id.btnQuit)
        btnQuit.setOnClickListener {
            finishAffinity() // closes the app completely
        }
    }
}
