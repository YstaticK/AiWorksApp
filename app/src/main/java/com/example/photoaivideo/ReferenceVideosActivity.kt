package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ReferenceVideosActivity : AppCompatActivity() {

    private lateinit var recyclerViewReferenceVideos: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()
    private lateinit var currentDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_videos)

        // Determine current directory (default = root reference_videos)
        val path = intent.getStringExtra("path")
        currentDir = if (path != null) File(path) else File(filesDir, "reference_videos")

        if (!currentDir.exists()) currentDir.mkdirs()
        if (path == null) {
            val exampleDir = File(currentDir, "Example Videos")
            if (!exampleDir.exists()) exampleDir.mkdirs()
        }

        recyclerViewReferenceVideos = findViewById(R.id.recyclerViewReferenceVideos)
        recyclerViewReferenceVideos.layoutManager = LinearLayoutManager(this)
        adapter = FolderAdapter(folders.toMutableList()) { folder ->
            val intent = Intent(this, ReferenceVideosActivity::class.java)
            intent.putExtra("path", folder.absolutePath)
            startActivity(intent)
        }
        recyclerViewReferenceVideos.adapter = adapter

        val btnAddReferenceVideoFolder = findViewById<FloatingActionButton>(R.id.btnAddReferenceVideoFolder)
        btnAddReferenceVideoFolder.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Subfolder")

            val input = EditText(this)
            input.hint = "Subfolder name"
            builder.setView(input)

            builder.setPositiveButton("Create") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val newFolder = File(currentDir, folderName)
                    if (!newFolder.exists()) {
                        newFolder.mkdirs()
                        loadFolders(currentDir)
                        adapter.updateData(folders.toMutableList())
                    }
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        loadFolders(currentDir)
    }

    private fun loadFolders(directory: File) {
        folders.clear()
        val subfolders = directory.listFiles { file -> file.isDirectory }
        if (subfolders != null) folders.addAll(subfolders)
        adapter.updateData(folders.toMutableList())
    }
}
