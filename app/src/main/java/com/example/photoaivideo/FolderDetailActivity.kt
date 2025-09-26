package com.example.photoaivideo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        val folderPath = intent.getStringExtra("folderPath") ?: return
        val folder = File(folderPath)

        val files = folder.listFiles()?.filter { it.isFile } ?: emptyList()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFiles)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = object : RecyclerView.Adapter<FileViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
                val imageView = ImageView(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(250, 250)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                return FileViewHolder(imageView)
            }

            override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
                val file = files[position]
                if (file.extension.lowercase() in listOf("jpg","jpeg","png")) {
                    val bmp = BitmapFactory.decodeFile(file.absolutePath)
                    holder.imageView.setImageBitmap(bmp)
                } else {
                    holder.imageView.setImageResource(R.drawable.ic_folder_placeholder)
                }
            }

            override fun getItemCount() = files.size
        }
    }

    class FileViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
