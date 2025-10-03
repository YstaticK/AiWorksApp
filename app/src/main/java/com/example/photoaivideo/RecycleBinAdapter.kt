package com.example.photoaivideo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class RecycleBinAdapter(
    private val context: Context,
    private val items: MutableList<File>,
    private val onSelectionChanged: (Set<File>) -> Unit
) : RecyclerView.Adapter<RecycleBinAdapter.BinViewHolder>() {

    private val selectedFiles = mutableSetOf<File>()

    inner class BinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val overlay: View = view.findViewById(R.id.selectionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generated_image, parent, false)
        return BinViewHolder(view)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        val file = items[position]
        Glide.with(context).load(file).into(holder.imageView)

        holder.overlay.visibility =
            if (selectedFiles.contains(file)) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            if (selectedFiles.isNotEmpty()) {
                toggleSelection(file)
                notifyItemChanged(position)
                onSelectionChanged(selectedFiles)
            } else {
                // open fullscreen preview
                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("filePath", file.absolutePath)
                context.startActivity(intent)
            }
        }

        holder.itemView.setOnLongClickListener {
            toggleSelection(file)
            notifyItemChanged(position)
            onSelectionChanged(selectedFiles)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    private fun toggleSelection(file: File) {
        if (selectedFiles.contains(file)) selectedFiles.remove(file)
        else selectedFiles.add(file)
    }

    fun clearSelection() {
        selectedFiles.clear()
        notifyDataSetChanged()
        onSelectionChanged(selectedFiles)
    }

    fun getSelectedFiles(): Set<File> = selectedFiles
}
