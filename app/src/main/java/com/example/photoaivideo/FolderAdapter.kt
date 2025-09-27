package com.example.photoaivideo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderAdapter(
    private val folders: MutableList<File>
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {

        val folder = folders[position]

        holder.name.text = folder.name



        // If folder is empty → show folder icon

        if (folder.listFiles()?.isEmpty() != false) {

            holder.image.setImageResource(R.drawable.ic_folder)

        } else {

            // Otherwise show the last item inside as preview

            val lastFile = folder.listFiles()?.lastOrNull()

            if (lastFile != null) {

                if (lastFile.extension.lowercase() in listOf("jpg", "jpeg", "png")) {

                    holder.image.setImageURI(Uri.fromFile(lastFile))

                } else {

                    holder.image.setImageResource(R.drawable.ic_folder) // fallback

                }

            } else {

                holder.image.setImageResource(R.drawable.ic_folder)

            }

        }

    }
    }

    override fun getItemCount(): Int = folders.size

    /** Replace current list and refresh the RecyclerView */
    fun updateData(newFolders: List<File>) {
        folders.clear()
        folders.addAll(newFolders)
        notifyDataSetChanged()
    }
}
