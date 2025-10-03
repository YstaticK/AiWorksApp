package com.example.photoaivideo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private val selectedFiles = mutableSetOf<File>()
    private lateinit var adapter: GeneratedImageAdapter
    private lateinit var btnDelete: FloatingActionButton
    private lateinit var btnCancel: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        val recycler = findViewById<RecyclerView>(R.id.recyclerViewLibrary)
        recycler.layoutManager = GridLayoutManager(this, 2)

        // Floating action buttons
        btnDelete = findViewById(R.id.btnDelete)
        btnCancel = findViewById(R.id.btnCancel)

        // Load generated images from app storage
        val imagesDir = File(getExternalFilesDir("images"), "misc")
        val files = (imagesDir.listFiles()?.filter { it.isFile } ?: emptyList())
            .sortedByDescending { it.lastModified() }

        adapter = GeneratedImageAdapter(
            context = this,
            files = files,
            request = null,
            selectable = true
        ) { file, isSelected ->
            if (isSelected) selectedFiles.add(file) else selectedFiles.remove(file)
            updateButtons()
        }

        recycler.adapter = adapter

        btnDelete.setOnClickListener {
            for (file in selectedFiles) {
                val recycleDir = File(getExternalFilesDir("images"), "recycle_bin")
                recycleDir.mkdirs()
                file.renameTo(File(recycleDir, file.name))
            }
            reload()
        }

        btnCancel.setOnClickListener {
            selectedFiles.clear()
            adapter.clearSelection()
            updateButtons()
        }

        updateButtons()
    }

    private fun updateButtons() {
        val visible = selectedFiles.isNotEmpty()
        btnDelete.visibility = if (visible) View.VISIBLE else View.GONE
        btnCancel.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun reload() {
        finish()
        startActivity(intent)
    }
}
