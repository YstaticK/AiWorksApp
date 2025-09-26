package com.example.photoaivideo

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderAdapter(
    private var folders: List<File>,
    private val context: Context
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName: TextView = itemView.findViewById(R.id.tvFolderName)
        val folderPreview: ImageView = itemView.findViewById(R.id.ivFolderPreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.folderName.text = folder.name

        // Check files in folder
        val files = folder.listFiles()
        if (files != null && files.isNotEmpty()) {
            val lastFile = files.maxByOrNull { it.lastModified() }
            if (lastFile != null && lastFile.isFile && lastFile.extension.lowercase() in listOf("jpg","jpeg","png","mp4","avi")) {
                try {
                    if (lastFile.extension.lowercase() in listOf("jpg","jpeg","png")) {
                        val bitmap = BitmapFactory.decodeFile(lastFile.absolutePath)
                        holder.folderPreview.setImageBitmap(bitmap)
                    } else {
                        holder.folderPreview.setImageResource(R.drawable.ic_folder_placeholder) // later: video thumbnail
                    }
                } catch (e: Exception) {
                    holder.folderPreview.setImageResource(R.drawable.ic_folder_placeholder)
                }
            }
        } else {
            holder.folderPreview.setImageResource(R.drawable.ic_folder_placeholder)
        }

        // Open folder on click
        holder.itemView.setOnClickListener {
            val intent = Intent(context, FolderDetailActivity::class.java)
            intent.putExtra("folderPath", folder.absolutePath)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = folders.size

    fun updateData(newFolders: List<File>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}
