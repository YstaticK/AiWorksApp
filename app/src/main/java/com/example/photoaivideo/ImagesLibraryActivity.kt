package com.example.photoaivideo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()
    private lateinit var rootDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        recyclerView = findViewById(R.id.recyclerImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        rootDir = File(getExternalFilesDir("images")!!.absolutePath)
        if (!rootDir.exists()) rootDir.mkdirs()

        // Ensure misc folder exists
        val miscDir = File(rootDir, "misc")
        if (!miscDir.exists()) miscDir.mkdirs()

        adapter = FolderAdapter(folders.toMutableList()) { folder ->
            val intent = Intent(this, FolderDetailActivity::class.java)
            intent.putExtra("path", folder.absolutePath)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        loadFolders()

        // Hook up FloatingActionButton for new folders
        val fab: FloatingActionButton = findViewById(R.id.btnNewFolder)
        fab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Image Folder")

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
        rootDir.listFiles()?.filter { it.isDirectory }?.let {
            folders.addAll(it)
        }
        adapter.updateData(folders.toMutableList())
    }
}
