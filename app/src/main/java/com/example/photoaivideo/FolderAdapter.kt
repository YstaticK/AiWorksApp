package com.example.photoaivideo

import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderAdapter(
    private var folders: MutableList<File>,
    private val onFolderClick: (File) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderIcon: ImageView = itemView.findViewById(R.id.ivFolderIcon)
        val folderName: TextView = itemView.findViewById(R.id.tvFolderName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.folderName.text = folder.name

        val files = folder.listFiles()?.sortedByDescending { it.lastModified() }
        if (!files.isNullOrEmpty()) {
            val latestFile = files.first()
            if (latestFile.extension.lowercase() in listOf("jpg", "jpeg", "png", "webp")) {
                val bitmap = BitmapFactory.decodeFile(latestFile.absolutePath)
                holder.folderIcon.setImageBitmap(bitmap)
            } else if (latestFile.extension.lowercase() in listOf("mp4", "mkv", "avi")) {
                val thumbnail = ThumbnailUtils.createVideoThumbnail(
                    latestFile.absolutePath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
                if (thumbnail != null) {
                    holder.folderIcon.setImageBitmap(thumbnail)
                } else {
                    holder.folderIcon.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            } else {
                holder.folderIcon.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        } else {
            holder.folderIcon.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener { onFolderClick(folder) }
    }

    override fun getItemCount(): Int = folders.size

    fun updateData(newFolders: MutableList<File>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}
