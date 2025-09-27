package com.example.photoaivideo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ReferenceVideosActivity : AppCompatActivity() {

    private lateinit var adapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_videos)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReferenceVideos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FolderAdapter(getFolders(this))
        recyclerView.adapter = adapter

        val btnAddFolder = findViewById<FloatingActionButton>(R.id.btnAddReferenceVideoFolder)
        btnAddFolder.setOnClickListener {
            val newFolder = File(getExternalFilesDir(null), "ReferenceVideos/NewFolder_${System.currentTimeMillis()}")
            if (newFolder.mkdirs()) {
                adapter.updateData(getFolders(this))
            }
        }
    }

    private fun getFolders(context: Context): MutableList<File> {
        val baseDir = File(context.getExternalFilesDir(null), "ReferenceVideos")
        if (!baseDir.exists()) baseDir.mkdirs()
        return baseDir.listFiles()?.filter { it.isDirectory }?.toMutableList() ?: mutableListOf()
    }
}
