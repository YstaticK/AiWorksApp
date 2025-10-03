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

class ImagePreviewActivity : AppCompatActivity() {
    private lateinit var file: File
    private var request: GenerationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        val imageView = findViewById<ImageView>(R.id.fullscreenImage)
        val btnUse = findViewById<Button>(R.id.btnUseReference)
        val btnSave = findViewById<Button>(R.id.btnSaveReference)
        val btnDelete = findViewById<Button>(R.id.btnDeleteImage)
        val dropdownHeader = findViewById<TextView>(R.id.dropdownHeader)
        val dropdownContent = findViewById<LinearLayout>(R.id.dropdownContent)

        val path = intent.getStringExtra("filePath") ?: return
        file = File(path)
        request = intent.getSerializableExtra("generationRequest") as? GenerationRequest

        Glide.with(this).load(file).into(imageView)

        // Dropdown collapsed by default
        dropdownContent.visibility = View.GONE
        dropdownHeader.text = "Show Details ▼"

        dropdownHeader.setOnClickListener {
            if (dropdownContent.visibility == View.VISIBLE) {
                dropdownContent.visibility = View.GONE
                dropdownHeader.text = "Show Details ▼"
            } else {
                dropdownContent.visibility = View.VISIBLE
                dropdownHeader.text = "Hide Details ▲"
            }
        }

        // Fill dropdown with request details if available
        request?.let {
            val details = """
                Prompt: ${it.prompts}
                Negative: ${it.negativePrompt}
                Size: ${it.width}x${it.height}
                Seed: ${it.seed ?: "N/A"}
                Model: ${it.model}
            """.trimIndent()
            findViewById<TextView>(R.id.dropdownText).text = details
        }

        btnUse.setOnClickListener {
            val intent = Intent(this, GenerateImageActivity::class.java)
            intent.putExtra("referenceUri", file.absolutePath)
            startActivity(intent)
        }

        btnSave.setOnClickListener {
            val refDir = File(getExternalFilesDir("images"), "references")
            refDir.mkdirs()
            val target = File(refDir, file.name)
            file.copyTo(target, overwrite = true)
            finish()
        }

        btnDelete.setOnClickListener {
            val recycleDir = File(getExternalFilesDir("images"), "recycle_bin")
            recycleDir.mkdirs()
            file.renameTo(File(recycleDir, file.name))
            finish()
        }
    }
}
