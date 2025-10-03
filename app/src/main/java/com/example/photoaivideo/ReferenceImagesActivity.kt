package com.example.photoaivideo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ReferenceImagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLibrary)
        val infoText = TextView(this).apply {
            text = "No reference images available"
            textSize = 16f
            setPadding(24, 24, 24, 24)
        }

        val refDir = File(getExternalFilesDir("images"), "reference_images")
        val files = refDir.listFiles()?.toList() ?: emptyList()

        if (files.isEmpty()) {
            recyclerView.visibility = RecyclerView.GONE
            setContentView(infoText)
        } else {
            recyclerView.visibility = RecyclerView.VISIBLE
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.adapter = GeneratedImageAdapter(this, files, null)
        }
    }
}
