package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class RecyclingBinActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val deletedFolders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycling_bin)

        recyclerView = findViewById(R.id.recyclerViewRecycleBin)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val recycleDir = File(filesDir, "recycle_bin")
        if (!recycleDir.exists()) recycleDir.mkdirs()

        deletedFolders.addAll(recycleDir.listFiles()?.toList() ?: emptyList())
        adapter = FolderAdapter(deletedFolders.toMutableList()) { }
        recyclerView.adapter = adapter
    }
}
