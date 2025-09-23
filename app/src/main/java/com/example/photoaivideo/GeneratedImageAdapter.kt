package com.example.photoaivideo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GeneratedImageAdapter(
    private val images: List<Int>, // for now, use drawable resource IDs or placeholder
    private val captions: List<String>
) : RecyclerView.Adapter<GeneratedImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageGenerated: ImageView = itemView.findViewById(R.id.imageGenerated)
        val txtImageOverlay: TextView = itemView.findViewById(R.id.txtImageOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generated_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageGenerated.setImageResource(images[position])
        holder.txtImageOverlay.text = captions[position]
    }

    override fun getItemCount(): Int = images.size
}
