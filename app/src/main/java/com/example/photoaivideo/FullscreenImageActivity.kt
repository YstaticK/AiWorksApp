package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.File

class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val imagePath = intent.getStringExtra("imagePath")
        val prompt = intent.getStringExtra("prompt") ?: "Unknown prompt"

        val imageView: ImageView = findViewById(R.id.fullscreenImageView)
        val btnUseAsRef: Button = findViewById(R.id.btnUseAsReference)
        val btnSaveAsRef: Button = findViewById(R.id.btnSaveAsReference)
        val btnDelete: Button = findViewById(R.id.btnDelete)
        val dropdownHeader: TextView = findViewById(R.id.dropdownHeader)
        val dropdownContent: LinearLayout = findViewById(R.id.dropdownContent)
        val promptText: TextView = findViewById(R.id.promptText)

        if (imagePath != null) {
            Glide.with(this).load(File(imagePath)).into(imageView)
        }

        // Dropdown behavior
        promptText.text = prompt
        dropdownContent.visibility = View.GONE
        dropdownHeader.setOnClickListener {
            dropdownContent.visibility =
                if (dropdownContent.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Use as reference
        btnUseAsRef.setOnClickListener {
            val resultIntent = Intent(this, GenerateImageActivity::class.java)
            resultIntent.putExtra("referenceImagePath", imagePath)
            startActivity(resultIntent)
        }

        // Save as reference
        btnSaveAsRef.setOnClickListener {
            if (imagePath != null) {
                FileUtils.saveToReference(File(imagePath), this)
            }
        }

        // Delete (move to recycle bin)
        btnDelete.setOnClickListener {
            if (imagePath != null) {
                FileUtils.moveToRecycleBin(File(imagePath), this)
                finish()
            }
        }
    }
}
