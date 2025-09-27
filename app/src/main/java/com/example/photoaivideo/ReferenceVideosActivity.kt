package com.example.photoaivideo

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ReferenceVideosActivity : AppCompatActivity() {

    private lateinit var recyclerViewReferenceVideos: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_videos)

        recyclerViewReferenceVideos = findViewById(R.id.recyclerViewReferenceVideos)
        recyclerViewReferenceVideos.layoutManager = LinearLayoutManager(this)
        adapter = FolderAdapter(folders)
        recyclerViewReferenceVideos.adapter = adapter

        val btnAddReferenceVideoFolder =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddReferenceVideoFolder)

        val rootDir = File(filesDir, "reference_videos")
        if (!rootDir.exists()) rootDir.mkdirs()

        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        // Create Example Videos folder only once after installation
        if (!prefs.getBoolean("exampleVideosCreated", false)) {
            val exampleDir = File(rootDir, "Example Videos")
            if (!exampleDir.exists()) exampleDir.mkdirs()
            prefs.edit().putBoolean("exampleVideosCreated", true).apply()
        }

        // Load initial folders
        loadFolders(rootDir)

        btnAddReferenceVideoFolder.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Folder")

            val input = EditText(this)
            input.hint = "Folder name"
            builder.setView(input)

            builder.setPositiveButton("Create") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val newFolder = File(rootDir, folderName)
                    if (!newFolder.exists()) {
                        newFolder.mkdirs()
                        folders.add(newFolder)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    private fun loadFolders(rootDir: File) {
        folders.clear()
        val list = rootDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        folders.addAll(list)
        adapter.notifyDataSetChanged()
    }
}
