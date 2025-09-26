package com.example.photoaivideo

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FolderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        val folderName = intent.getStringExtra("FOLDER_NAME") ?: "Unknown"
        title = folderName

        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        rv.layoutManager = GridLayoutManager(this, 3)

        // Placeholder empty adapter for now
        rv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = ImageView(parent.context)
                view.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                return object : RecyclerView.ViewHolder(view) {}
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
            override fun getItemCount(): Int = 0
        }
    }
}
