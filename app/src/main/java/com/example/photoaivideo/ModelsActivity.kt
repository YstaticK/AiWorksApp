package com.example.photoaivideo

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ModelsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        recyclerView = findViewById(R.id.recyclerViewModels)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FolderAdapter(folders.toMutableList())
        recyclerView.adapter = adapter

        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAddModel)

        val rootDir = File(filesDir, "models")
        if (!rootDir.exists()) rootDir.mkdirs()
        loadFolders(rootDir)

        btnAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Model")

            val input = EditText(this)
            input.hint = "Model name"
            builder.setView(input)

            builder.setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val newDir = File(rootDir, name)
                    if (!newDir.exists()) {
                        newDir.mkdirs()
                        folders.add(newDir)
                        adapter.notifyItemInserted(folders.size - 1)
                    } else {
                        Toast.makeText(this, "Model already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    private fun loadFolders(rootDir: File) {
        folders.clear()
        rootDir.listFiles()?.filter { it.isDirectory }?.let { folders.addAll(it) }
        adapter.notifyDataSetChanged()
    }
}
