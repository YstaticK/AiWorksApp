package com.example.photoaivideo

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class RecycleBinActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecycleBinAdapter
    private val deletedItems = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_bin)

        recyclerView = findViewById(R.id.recyclerViewRecycleBin)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val recycleBinDir = File(filesDir, "recycle_bin")
        if (!recycleBinDir.exists()) recycleBinDir.mkdirs()

        deletedItems.addAll(recycleBinDir.listFiles()?.toList() ?: emptyList())

        adapter = RecycleBinAdapter(deletedItems,
            onRestore = { file -> restoreItem(file) },
            onDelete = { file -> deleteItemPermanently(file) }
        )
        recyclerView.adapter = adapter
    }

    private fun restoreItem(file: File) {
        val parentName = file.name.substringBefore("_")
        val targetDir = File(filesDir, parentName)
        if (!targetDir.exists()) targetDir.mkdirs()

        val restored = file.renameTo(File(targetDir, file.name))
        if (restored) {
            deletedItems.remove(file)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Restored: ${file.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteItemPermanently(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete Permanently")
            .setMessage("Are you sure you want to permanently delete ${file.name}?")
            .setPositiveButton("Delete") { _, _ ->
                if (file.deleteRecursively()) {
                    deletedItems.remove(file)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Deleted: ${file.name}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
