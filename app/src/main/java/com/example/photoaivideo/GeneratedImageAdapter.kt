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

class GeneratedImageAdapter(
    private val context: Context,
    private val files: List<File>,
    private val request: GenerationRequest? = null,
    private val selectable: Boolean = false,
    private val selectionListener: ((File, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<GeneratedImageAdapter.ViewHolder>() {

    private val selectedFiles = mutableSetOf<File>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val overlay: View = view.findViewById(R.id.selectionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_generated_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        Glide.with(context).load(file).into(holder.imageView)

        holder.overlay.visibility = if (selectable && selectedFiles.contains(file)) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            if (selectable) {
                toggleSelection(file)
                notifyItemChanged(position)
                selectionListener?.invoke(file, selectedFiles.contains(file))
            } else {
                // Open fullscreen preview
                val intent = Intent(context, ImagePreviewActivity::class.java).apply {
                    putExtra("filePath", file.absolutePath)
                    request?.let { putExtra("generationRequest", it) }
                }
                context.startActivity(intent)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (selectable) {
                toggleSelection(file)
                notifyItemChanged(position)
                selectionListener?.invoke(file, selectedFiles.contains(file))
                true
            } else false
        }
    }

    override fun getItemCount(): Int = files.size

    private fun toggleSelection(file: File) {
        if (selectedFiles.contains(file)) selectedFiles.remove(file) else selectedFiles.add(file)
    }

    fun clearSelection() {
        selectedFiles.clear()
        notifyDataSetChanged()
    }

    fun getSelectedFiles(): Set<File> = selectedFiles
}
