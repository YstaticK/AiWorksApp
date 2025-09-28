package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibraryFolderAdapter
    private lateinit var rootDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        recyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        rootDir = File(getExternalFilesDir("images")!!.absolutePath)
        if (!rootDir.exists()) rootDir.mkdirs()

        // Ensure misc folder always exists
        val miscDir = File(rootDir, "misc")
        if (!miscDir.exists()) miscDir.mkdirs()

        adapter = LibraryFolderAdapter(this, mutableListOf())
        recyclerView.adapter = adapter

        loadFolders()
    }

    override fun onResume() {
        super.onResume()
        // ✅ Refresh contents when user returns
        loadFolders()
    }

    private fun loadFolders() {
        val folders = rootDir.listFiles()?.filter { folder ->
            folder.isDirectory && (folder.listFiles()?.isNotEmpty() == true)
        }?.toMutableList() ?: mutableListOf()

        adapter.updateData(folders)
    }
}
