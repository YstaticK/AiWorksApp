package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnReferenceLibrary = findViewById<Button>(R.id.btnReferenceLibrary)
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)

        btnRecycleBin.setOnClickListener {

            startActivity(Intent(this, RecycleBinActivity::class.java))

        }
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)
        btnRecycleBin.setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
        }
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)
        btnRecycleBin.setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
        }

        btnReferenceLibrary.setOnClickListener {
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)

        btnRecycleBin.setOnClickListener {

            startActivity(Intent(this, RecycleBinActivity::class.java))

        }
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)
        btnRecycleBin.setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
        }
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)
        btnRecycleBin.setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
        }
        val btnRecycleBin = findViewById<Button>(R.id.btnRecycleBin)

        btnRecycleBin.setOnClickListener {

            startActivity(Intent(this, RecyclingBinActivity::class.java))

        }

            startActivity(Intent(this, ReferenceLibraryActivity::class.java))

        }
        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        btnLibrary.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        val btnImage: Button = findViewById(R.id.btnImage)
        val btnVideo: Button = findViewById(R.id.btnVideo)

        btnImage.setOnClickListener {
            val intent = Intent(this, GenerateImageActivity::class.java)
            startActivity(intent)
        }

        btnVideo.setOnClickListener {
            val intent = Intent(this, GenerateVideoActivity::class.java)
            startActivity(intent)
        }
    }
}
