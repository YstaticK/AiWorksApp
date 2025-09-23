package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GeneratedImageResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_results)

        val recyclerView = findViewById<RecyclerView>(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        // TODO: Hook adapter once generation logic is ready
    }
}
