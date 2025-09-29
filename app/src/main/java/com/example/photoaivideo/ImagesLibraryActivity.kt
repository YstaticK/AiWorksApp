package com.example.photoaivideo

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rootDir: File
    private lateinit var adapter: LibraryFolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        recyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        rootDir = File(getExternalFilesDir("images")!!.absolutePath)
        if (!rootDir.exists()) rootDir.mkdirs()

        // Ensure misc exists
        val miscDir = File(rootDir, "misc")
        if (!miscDir.exists()) miscDir.mkdirs()

        // Setup adapter
        val folders = loadFolders()
        adapter = LibraryFolderAdapter(this, folders)
        recyclerView.adapter = adapter

        // Hook up FAB if present in layout (won't crash if missing)
        val fab = try {
            findViewById<FloatingActionButton>(R.id.btnAddImageFolder)
        } catch (_: Throwable) { null }

        fab?.setOnClickListener {
            val input = EditText(this).apply { hint = "Folder name" }
            AlertDialog.Builder(this)
                .setTitle("New Image Folder")
                .setView(input)
                .setPositiveButton("Create") { _, _ ->
                    val name = input.text.toString().trim()
                    if (name.isNotEmpty()) {
                        val newFolder = File(rootDir, name)
                        if (!newFolder.exists()) {
                            newFolder.mkdirs()
                            adapter = LibraryFolderAdapter(this, loadFolders())
                            recyclerView.adapter = adapter
                        } else {
                            ErrorUtils.showErrorDialog(this, "A folder named \"$name\" already exists.")
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun loadFolders(): MutableList<File> {
        // Show ALL subfolders (including empty)
        return rootDir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name.lowercase() }
            ?.toMutableList()
            ?: mutableListOf()
    }
}
