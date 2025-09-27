package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)
        val btnAddImageFolder = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddImageFolder)
        btnAddImageFolder.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("New Image Folder")
            val input = android.widget.EditText(this)
            input.hint = "Folder name"
            builder.setView(input)
            builder.setPositiveButton("Create") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val newFolder = java.io.File(filesDir, "images/$folderName")
                    if (!newFolder.exists()) newFolder.mkdirs()
                    adapter.updateData(getFolders().toMutableList())
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        recyclerView = findViewById(R.id.recyclerViewImagesLibrary)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val rootDir = File(filesDir, "images_library")
        if (!rootDir.exists()) rootDir.mkdirs()

        folders.addAll(rootDir.listFiles()?.filter { it.isDirectory } ?: emptyList())

        adapter = FolderAdapter(folders.toMutableList()) { folder ->
            val intent = Intent(this, FolderDetailActivity::class.java)
            intent.putExtra("path", folder.absolutePath)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}
