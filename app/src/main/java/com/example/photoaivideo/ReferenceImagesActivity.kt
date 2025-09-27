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

class ReferenceImagesActivity : AppCompatActivity() {

    private lateinit var recyclerViewReferenceImages: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()
    private lateinit var currentDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_images)

        // Determine current directory (default = root reference_images)
        val path = intent.getStringExtra("path")
        currentDir = if (path != null) File(path) else File(filesDir, "reference_images")

        if (!currentDir.exists()) currentDir.mkdirs()
        val exampleDir = File(currentDir, "Example Images")
        if (!exampleDir.exists()) exampleDir.mkdirs()

        recyclerViewReferenceImages = findViewById(R.id.recyclerViewReferenceImages)
        recyclerViewReferenceImages.layoutManager = LinearLayoutManager(this)
        adapter = FolderAdapter(folders.toMutableList()) { folder ->
            val intent = Intent(this, ReferenceImagesActivity::class.java)
            intent.putExtra("path", folder.absolutePath)
            startActivity(intent)
        }
        recyclerViewReferenceImages.adapter = adapter

        val btnAddReferenceImageFolder = findViewById<FloatingActionButton>(R.id.btnAddReferenceImageFolder)
        btnAddReferenceImageFolder.setOnClickListener {
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
