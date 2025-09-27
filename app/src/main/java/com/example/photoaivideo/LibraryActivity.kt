package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class LibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter
    private val folders = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        recyclerView = findViewById(R.id.recyclerViewLibrary)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val rootDir = File(filesDir, "library")
        if (!rootDir.exists()) rootDir.mkdirs()

        folders.addAll(rootDir.listFiles()?.filter { it.isDirectory } ?: emptyList())

        adapter = FolderAdapter(folders.toMutableList()) { folder ->
            val intent = Intent(this, FolderDetailActivity::class.java)
            intent.putExtra("path", folder.absolutePath)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
    fun openImagesLibrary(view: android.view.View) {
        startActivity(Intent(this, ImagesLibraryActivity::class.java))
    }
    
    fun openVideosLibrary(view: android.view.View) {
        startActivity(Intent(this, VideosLibraryActivity::class.java))
    }
}
