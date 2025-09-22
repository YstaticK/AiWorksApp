package com.example.photoaivideo

import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GeneratedVideoResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_video_results)

        val tvVideoResults = findViewById<TextView>(R.id.tvVideoResultsTitle)
        val grid = findViewById<GridView>(R.id.gridGeneratedVideos)

        val uris = intent.getStringArrayListExtra("generatedVideos") ?: arrayListOf()

        grid.adapter = GeneratedVideoAdapter(this, uris)
    }
}
