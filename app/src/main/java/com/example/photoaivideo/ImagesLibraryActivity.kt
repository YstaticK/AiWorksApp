package com.example.photoaivideo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ImagesLibraryActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: GeneratedImageAdapter
    private lateinit var btnDeleteSelected: FloatingActionButton
    private lateinit var btnCancelSelection: FloatingActionButton

    private val selectedFiles = mutableSetOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_library)

        recycler = findViewById(R.id.recyclerViewLibrary)
        recycler.layoutManager = GridLayoutManager(this, 2)

        btnDeleteSelected = findViewById(R.id.btnDeleteSelected)
        btnCancelSelection = findViewById(R.id.btnCancelSelection)

        btnDeleteSelected.setOnClickListener { deleteSelected() }
        btnCancelSelection.setOnClickListener { cancelSelection() }

        refreshList()
        updateButtons()
    }

    private fun refreshList() {
        val imagesDir = File(getExternalFilesDir("images"), "misc")
        val files = (imagesDir.listFiles()?.filter { it.isFile } ?: emptyList())
            .sortedByDescending { it.lastModified() }

        selectedFiles.clear()

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
    }

    private fun deleteSelected() {
        if (selectedFiles.isEmpty()) return
        val recycleDir = File(getExternalFilesDir("images"), "recycle_bin")
        recycleDir.mkdirs()
        for (f in selectedFiles) {
            f.renameTo(File(recycleDir, f.name))
        }
        refreshList()
        updateButtons()
    }

    private fun cancelSelection() {
        selectedFiles.clear()
        adapter.clearSelection()
        updateButtons()
    }

    private fun updateButtons() {
        val visible = selectedFiles.isNotEmpty()
        btnDeleteSelected.visibility = if (visible) View.VISIBLE else View.GONE
        btnCancelSelection.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
