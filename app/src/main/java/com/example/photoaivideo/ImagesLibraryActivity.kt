package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        recyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        loadFolders()
    }

    private fun loadFolders() {
        val rootDir = File(getExternalFilesDir("images")!!.absolutePath)
        if (!rootDir.exists()) rootDir.mkdirs()

        val miscDir = File(rootDir, "misc")
        if (!miscDir.exists()) miscDir.mkdirs()

        val folders = rootDir.listFiles()
            ?.filter { it.isDirectory && it.listFiles()?.isNotEmpty() == true }
            ?.toMutableList()
            ?: mutableListOf()

        recyclerView.adapter = LibraryFolderAdapter(this, folders)
    }
}
