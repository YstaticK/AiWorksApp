package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ReferenceImagesActivity : AppCompatActivity() {

    private lateinit var recyclerViewReferenceImages: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_images)

        recyclerViewReferenceImages = findViewById(R.id.recyclerViewReferenceImages)
        recyclerViewReferenceImages.layoutManager = LinearLayoutManager(this)
        adapter = FolderAdapter(folders)
        recyclerViewReferenceImages.adapter = adapter

        val btnAddReferenceImageFolder = findViewById<Button>(R.id.btnAddReferenceImageFolder)

        // Ensure base dir exists
        val rootDir = File(filesDir, "reference_images")
        if (!rootDir.exists()) rootDir.mkdirs()

        loadFolders(rootDir)

        btnAddReferenceImageFolder.setOnClickListener {
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
