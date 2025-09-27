package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
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
        adapter = FolderAdapter(folders.toMutableList())
        recyclerViewReferenceVideos.adapter = adapter

        val btnAddReferenceVideoFolder = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddReferenceVideoFolder)

        // Ensure base dir exists
        val rootDir = File(filesDir, "reference_videos")
        if (!rootDir.exists()) rootDir.mkdirs()
        val exampleDir = File(rootDir, "Example Videos");
        if (!exampleDir.exists()) exampleDir.mkdirs()

        loadFolders(rootDir)

        btnAddReferenceVideoFolder.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Folder")

            val input = EditText(this)
            input.hint = "Folder name"
            builder.setView(input)

            builder.setPositiveButton("Create") { _, _ ->
                val newFolder = File(rootDir, input.text.toString());
                if (!newFolder.exists()) newFolder.mkdirs();
                loadFolders(rootDir);
                adapter.updateData(folders.toMutableList())
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
        val existing = rootDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        folders.addAll(existing)
    }
}
