package com.example.photoaivideo

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class VideosLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerViewVideosLibrary: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()
    private lateinit var rootDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos_library)

        rootDir = File(filesDir, "videos_library")
        if (!rootDir.exists()) rootDir.mkdirs()

        recyclerViewVideosLibrary = findViewById(R.id.recyclerViewVideosLibrary)
        recyclerViewVideosLibrary.layoutManager = LinearLayoutManager(this)

        adapter = FolderAdapter(folders.toMutableList()) { folder ->
            val intent = Intent(this, FolderDetailActivity::class.java)
            intent.putExtra("path", folder.absolutePath)
            startActivity(intent)
        }
        recyclerViewVideosLibrary.adapter = adapter

        loadFolders()

        val btnAddVideoFolder = findViewById<FloatingActionButton>(R.id.btnAddVideoFolder)
        btnAddVideoFolder.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Video Folder")

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
                        adapter.updateData(folders.toMutableList())
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
}
