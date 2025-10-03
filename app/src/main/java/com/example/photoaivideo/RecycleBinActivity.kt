package com.example.photoaivideo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class RecycleBinActivity : AppCompatActivity() {
    private val selectedFiles = mutableSetOf<File>()
    private lateinit var adapter: GeneratedImageAdapter
    private lateinit var btnRestore: FloatingActionButton
    private lateinit var btnDelete: FloatingActionButton
    private lateinit var btnCancel: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_bin)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRecycleBin)
        val emptyText = findViewById<TextView>(R.id.txtEmptyRecycleBin)

        val recycleDir = File(getExternalFilesDir("images"), "recycle_bin")
        val files = recycleDir.listFiles()?.toList() ?: emptyList()

        btnRestore = findViewById(R.id.btnRestore)
        btnDelete = findViewById(R.id.btnDelete)
        btnCancel = findViewById(R.id.btnCancel)

        fun updateButtons() {
            val visible = selectedFiles.isNotEmpty()
            btnRestore.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
            btnDelete.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
            btnCancel.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
        }

        if (files.isEmpty()) {
            recyclerView.visibility = RecyclerView.GONE
            emptyText.text = "Recycle Bin is empty"
        } else {
            recyclerView.visibility = RecyclerView.VISIBLE
            emptyText.text = ""
            recyclerView.layoutManager = GridLayoutManager(this, 2)

            adapter = GeneratedImageAdapter(this, files, null, selectable = true) { file, isSelected ->
                if (isSelected) selectedFiles.add(file) else selectedFiles.remove(file)
                updateButtons()
            }
            recyclerView.adapter = adapter
        }

        btnRestore.setOnClickListener {
            for (file in selectedFiles) {
                val targetDir = File(getExternalFilesDir("images"), "misc")
                targetDir.mkdirs()
                file.renameTo(File(targetDir, file.name))
            }
            finish(); startActivity(intent) // reload
        }

        btnDelete.setOnClickListener {
            for (file in selectedFiles) file.delete()
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
