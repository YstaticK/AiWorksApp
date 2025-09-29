package com.example.photoaivideo

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_images)

        recyclerView = findViewById(R.id.recyclerFolderImages)
        tvEmpty = findViewById(R.id.tvEmptyFolder)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        checkAndLoadImages()
    }

    private fun checkAndLoadImages() {
        if (!PermissionsHelper.hasStoragePermission(this)) {
            Toast.makeText(this, "Storage permission required to open this folder.", Toast.LENGTH_LONG).show()
            PermissionsHelper.requestStoragePermission(this)
            return
        }

        val folderPath = intent.getStringExtra("folderPath")
        val folder = File(folderPath ?: "")

        if (folder.exists() && folder.isDirectory) {
            val images = folder.listFiles { file ->
                file.isFile && (file.extension.equals("png", true) ||
                        file.extension.equals("jpg", true) ||
                        file.extension.equals("jpeg", true))
            }?.toList() ?: emptyList()

            if (images.isNotEmpty()) {
                recyclerView.adapter = FolderImagesAdapter(this, images)
                tvEmpty.visibility = TextView.GONE
            } else {
                tvEmpty.visibility = TextView.VISIBLE
            }
        } else {
            tvEmpty.visibility = TextView.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionsHelper.REQUEST_CODE) {
            // Retry loading images if permission granted
            checkAndLoadImages()
        }
    }
}
