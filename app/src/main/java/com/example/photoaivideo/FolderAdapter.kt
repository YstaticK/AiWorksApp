package com.example.photoaivideo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderAdapter(
    private val folders: List<File>
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        holder.title.text = folder.name
        holder.itemView.setOnClickListener {
            val ctx = holder.itemView.context
            val intent = Intent(ctx, FolderDetailActivity::class.java)
            intent.putExtra("FOLDER_NAME", folder.name)
            intent.putExtra("FOLDER_PATH", folder.absolutePath)
            ctx.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = folders.size
}
