package com.example.photoaivideo

import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GeneratedResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_results)

        val tvResultsTitle = findViewById<TextView>(R.id.tvResultsTitle)
        val grid = findViewById<GridView>(R.id.gridGeneratedImages)

        val uris = intent.getStringArrayListExtra("generatedUris") ?: arrayListOf()

        // simple adapter: just show gray placeholders with text overlay
        grid.adapter = GeneratedImageAdapter(this, uris)
    }
}
