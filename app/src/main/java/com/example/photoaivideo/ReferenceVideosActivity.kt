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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_videos)

        recyclerView = findViewById(R.id.recyclerViewReferenceVideos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FolderAdapter(folders)
        recyclerView.adapter = adapter

        val btnAddReferenceVideoFolder = findViewById<Button>(R.id.btnAddReferenceVideoFolder)

        // Ensure base dir exists
        val rootDir = File(filesDir, "reference_videos")
        if (!rootDir.exists()) rootDir.mkdirs()

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
        val existing = rootDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        folders.addAll(existing)
    }
}
