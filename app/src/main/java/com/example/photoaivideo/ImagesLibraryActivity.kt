package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Root images dir
        val rootDir = File(getExternalFilesDir("images")!!.absolutePath)
        if (!rootDir.exists()) rootDir.mkdirs()

        // Ensure misc folder always exists
        val miscDir = File(rootDir, "misc")
        if (!miscDir.exists()) miscDir.mkdirs()

        // Get all subfolders
        val folders = rootDir.listFiles()?.filter { it.isDirectory }?.toMutableList() ?: mutableListOf()

        recyclerView.adapter = LibraryFolderAdapter(this, folders)
    }
}
