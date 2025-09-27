package com.example.photoaivideo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import java.io.File

class VideosLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()
    private lateinit var rootDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos_library)

        rootDir = File(filesDir, "videos_library")
        if (!rootDir.exists()) rootDir.mkdirs()

        recyclerView = findViewById(R.id.recyclerViewVideosLibrary)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FolderAdapter(folders.toMutableList()) { folder ->
            showFilesInFolder(folder)
        }
        recyclerView.adapter = adapter

        loadFolders()

        val btnAddFolder = findViewById<FloatingActionButton>(R.id.btnAddVideoFolder)
        btnAddFolder.setOnClickListener {
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
                        loadFolders()
                    }
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    private fun loadFolders() {
        folders.clear()
        rootDir.listFiles()?.filter { it.isDirectory }?.let { folders.addAll(it) }
        adapter.updateData(folders.toMutableList())
    }

    private fun showFilesInFolder(folder: File) {
        val files = folder.listFiles() ?: emptyArray()
        val fileNames = files.map { it.name }

        AlertDialog.Builder(this)
            .setTitle("Files in ${folder.name}")
            .setItems(fileNames.toTypedArray()) { _, which ->
                val selectedFile = files[which]
                openFile(selectedFile)
            }
            .setPositiveButton("OK", null)
            .show()
    }

    private fun openFile(file: File) {
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_VIEW)

        when {
            file.name.endsWith(".mp4", true) || file.name.endsWith(".mkv", true) -> {
                intent.setDataAndType(uri, "video/*")
            }
            else -> {
                Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show()
                return
            }
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Open with"))
    }
}
