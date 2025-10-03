package com.example.photoaivideo

import android.os.Bundle
import android.view.View
import android.widget.TextView
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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLibrary)
        val emptyText = TextView(this).apply {
            text = "No images in library"
            visibility = View.GONE
        }
        (recyclerView.parent as? ViewGroup)?.addView(emptyText)

        val miscDir = File(getExternalFilesDir("images"), "misc")
        val files = miscDir.listFiles()?.toList() ?: emptyList()

        btnDelete = findViewById(R.id.btnDelete)
        btnCancel = findViewById(R.id.btnCancel)

        fun updateButtons() {
            val visible = selectedFiles.isNotEmpty()
            btnDelete.visibility = if (visible) View.VISIBLE else View.GONE
            btnCancel.visibility = if (visible) View.VISIBLE else View.GONE
        }

        if (files.isEmpty()) {
            recyclerView.visibility = RecyclerView.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = RecyclerView.VISIBLE
            emptyText.visibility = View.GONE
            recyclerView.layoutManager = GridLayoutManager(this, 2)

            adapter = GeneratedImageAdapter(this, files, null, selectable = true) { file, isSelected ->
                if (isSelected) selectedFiles.add(file) else selectedFiles.remove(file)
                updateButtons()
            }
            recyclerView.adapter = adapter
        }

        btnDelete.setOnClickListener {
            val recycleDir = File(getExternalFilesDir("images"), "recycle_bin")
            recycleDir.mkdirs()
            for (file in selectedFiles) {
                file.renameTo(File(recycleDir, file.name))
            }
            finish(); startActivity(intent) // reload
        }

        btnCancel.setOnClickListener {
            selectedFiles.clear()
            adapter.clearSelection()
            updateButtons()
        }

        updateButtons()
    }
}
