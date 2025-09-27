package com.example.photoaivideo

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderAdapter(private val folders: MutableList<File>) :
    RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.folderName)
        val image: ImageView = view.findViewById(R.id.folderImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.name.text = folder.name

        val files = folder.listFiles()
        if (files.isNullOrEmpty()) {
            holder.image.setImageResource(R.drawable.ic_folder)
        } else {
            val lastFile = files.lastOrNull()
            if (lastFile != null && lastFile.extension.lowercase() in listOf("jpg", "jpeg", "png")) {
                holder.image.setImageURI(Uri.fromFile(lastFile))
            } else {
                holder.image.setImageResource(R.drawable.ic_folder)
            }
        }
    }

    override fun getItemCount(): Int = folders.size

    fun updateData(newFolders: List<File>) {
        folders.clear()
        folders.addAll(newFolders)
        notifyDataSetChanged()
    }
}
