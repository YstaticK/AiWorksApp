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

        val rootDir = File(getExternalFilesDir("images")!!.absolutePath)
        if (!rootDir.exists()) rootDir.mkdirs()

        val miscDir = File(rootDir, "misc")
        if (!miscDir.exists()) miscDir.mkdirs()

        val folders = mutableListOf<File>()
        folders.add(miscDir)

        val otherFolders = rootDir.listFiles()
            ?.filter { it.isDirectory && it.name != "misc" && it.listFiles()?.isNotEmpty() == true }
            ?.toMutableList()
            ?: mutableListOf()

        folders.addAll(otherFolders)

        recyclerView.adapter = LibraryFolderAdapter(this, folders)
    }
}
