package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderDetailActivity : AppCompatActivity() {

    private lateinit var recyclerViewFiles: RecyclerView
    private lateinit var adapter: FileAdapter
    private val files = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        recyclerViewFiles = findViewById(R.id.recyclerViewFiles)
        recyclerViewFiles.layoutManager = LinearLayoutManager(this)

        val path = intent.getStringExtra("path")
        if (path != null) {
            val folder = File(path)
            if (folder.exists() && folder.isDirectory) {
                files.addAll(folder.listFiles()?.filter { it.isFile } ?: emptyList())
            }
        }

        adapter = FileAdapter(files)
        recyclerViewFiles.adapter = adapter
    }
}
