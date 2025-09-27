package com.example.photoaivideo

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private lateinit var currentDir: File
    private val folders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        currentDir = File(filesDir, "images_library")
        if (!currentDir.exists()) currentDir.mkdirs()

        recyclerView = findViewById(R.id.recyclerViewImagesLibrary)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FolderAdapter(folders.toMutableList()) { /* no subfolders here */ }
        recyclerView.adapter = adapter

        val btnAddImageFolder = findViewById<FloatingActionButton>(R.id.btnAddImageFolder)
        btnAddImageFolder.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Folder")

            val input = EditText(this)
            input.hint = "Folder name"
            builder.setView(input)

            builder.setPositiveButton("Create") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val newFolder = File(currentDir, folderName)
                    if (!newFolder.exists()) {
                        newFolder.mkdirs()
                        folders.add(newFolder)
                        adapter.updateData(folders.toMutableList())
                    }
                }
            }

            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        loadFolders()
    }

    private fun loadFolders() {
        folders.clear()
        currentDir.listFiles()?.filter { it.isDirectory }?.let { folders.addAll(it) }
        adapter.updateData(folders.toMutableList())
    }
}
