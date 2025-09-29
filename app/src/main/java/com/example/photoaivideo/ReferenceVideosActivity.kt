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

        try {
            // Base path inside app-private storage
            val baseDir = File(getExternalFilesDir("references"), "videos")
            if (!baseDir.exists()) baseDir.mkdirs()

            val path = intent.getStringExtra("path")
            currentDir = if (path != null) File(path) else baseDir

            if (!currentDir.exists()) currentDir.mkdirs()

            // Example folder only in root
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

            val btnAddReferenceVideoFolder =
                findViewById<FloatingActionButton>(R.id.btnAddReferenceVideoFolder)

            btnAddReferenceVideoFolder.setOnClickListener {
                try {
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
                } catch (e: Exception) {
                    ErrorUtils.showErrorDialog(this, "Failed to create folder:\n${e.message}")
                }
            }

            loadFolders(currentDir)
        } catch (e: Exception) {
            ErrorUtils.showErrorDialog(this, "Initialization failed:\n${e.message}")
        }
    }

    private fun loadFolders(directory: File) {
        try {
            folders.clear()
            val subfolders = directory.listFiles { file -> file.isDirectory }
            if (subfolders != null) folders.addAll(subfolders)
            adapter.updateData(folders.toMutableList())
        } catch (e: Exception) {
            ErrorUtils.showErrorDialog(this, "Failed to load folders:\n${e.message}")
        }
    }
}
