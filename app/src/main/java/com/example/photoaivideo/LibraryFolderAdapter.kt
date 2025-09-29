package com.example.photoaivideo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class LibraryFolderAdapter(
    private val context: Context,
    private val folders: MutableList<File>
) : RecyclerView.Adapter<LibraryFolderAdapter.FolderViewHolder>() {

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        holder.folderIcon.setImageResource(android.R.drawable.ic_menu_gallery)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FolderImagesActivity::class.java)
            intent.putExtra("folderPath", folder.absolutePath)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = folders.size

    // 🔹 Allow dynamic updates (same pattern as FolderAdapter)
    fun updateData(newFolders: List<File>) {
        folders.clear()
        folders.addAll(newFolders)
        notifyDataSetChanged()
    }
}
