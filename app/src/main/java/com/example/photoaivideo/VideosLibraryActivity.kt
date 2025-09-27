package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class VideosLibraryActivity : AppCompatActivity() {

    private lateinit var adapter: FolderAdapter
    private lateinit var folderDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos_library)

        folderDir = File(getExternalFilesDir(null), "Videos")
        if (!folderDir.exists()) folderDir.mkdirs()

        val misc = File(folderDir, "Misc. Videos")
        if (!misc.exists()) misc.mkdirs()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFolders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FolderAdapter(getFolders().toMutableList())
        recyclerView.adapter = adapter

        val btnCreate = findViewById<Button>(R.id.btnCreateFolder)
        btnCreate.setOnClickListener {
            showCreateFolderDialog()
        }
    }

    private fun getFolders(): List<File> {
        return folderDir.listFiles()?.filter { it.isDirectory }?.sortedBy { it.name } ?: emptyList()
    }

    private fun showCreateFolderDialog() {
        val input = android.widget.EditText(this)
        AlertDialog.Builder(this)
            .setTitle("New Video Folder")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val newFolder = File(folderDir, folderName)
                    if (!newFolder.exists()) newFolder.mkdirs()
                    adapter.updateData(getFolders())
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
