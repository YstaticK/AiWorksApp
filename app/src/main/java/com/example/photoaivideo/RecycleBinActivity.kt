package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class RecycleBinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_bin)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRecycleBin)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // For now just show a placeholder
        val emptyText = findViewById<TextView>(R.id.txtEmptyRecycleBin)
        emptyText.text = "Recycle Bin is empty"
    }
}
