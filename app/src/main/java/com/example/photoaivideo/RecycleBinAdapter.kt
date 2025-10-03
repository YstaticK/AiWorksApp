package com.example.photoaivideo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class RecycleBinAdapter(
    private val items: MutableList<File>,
    private val selectable: Boolean = false,
    private val selectionListener: ((File, Boolean) -> Unit)? = null,
    private val onRestore: (File) -> Unit,
    private val onDelete: (File) -> Unit
) : RecyclerView.Adapter<RecycleBinAdapter.BinViewHolder>() {

    private val selectedFiles = mutableSetOf<File>()

    class BinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.textDeletedItemName)
        val overlay: View = view.findViewById(R.id.selectionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycle_bin, parent, false)
        return BinViewHolder(view)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        val file = items[position]
        holder.textName.text = file.name

        if (selectable && selectedFiles.contains(file)) {
            holder.overlay.visibility = View.VISIBLE
        } else {
            holder.overlay.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (selectable) {
                toggleSelection(file)
                notifyItemChanged(position)
                selectionListener?.invoke(file, selectedFiles.contains(file))
            }
        }

        holder.itemView.setOnLongClickListener {
            if (selectable) {
                toggleSelection(file)
                notifyItemChanged(position)
                selectionListener?.invoke(file, selectedFiles.contains(file))
                true
            } else {
                // single restore if not in multi-select mode
                onRestore(file)
                true
            }
        }

        // Single tap restore/delete if not in multi-select mode
        if (!selectable) {
            holder.itemView.setOnClickListener { onRestore(file) }
            holder.itemView.setOnLongClickListener {
                onDelete(file)
                true
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun toggleSelection(file: File) {
        if (selectedFiles.contains(file)) selectedFiles.remove(file)
        else selectedFiles.add(file)
    }

    fun getSelectedFiles(): Set<File> = selectedFiles

    fun clearSelection() {
        selectedFiles.clear()
        notifyDataSetChanged()
    }
}
