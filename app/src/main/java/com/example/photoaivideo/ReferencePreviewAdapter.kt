package com.example.photoaivideo

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ReferencePreviewAdapter(private val uris: List<Uri>) :
    RecyclerView.Adapter<ReferencePreviewAdapter.PreviewViewHolder>() {

    class PreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivGeneratedImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generated_image, parent, false)
        return PreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        holder.imageView.setImageURI(uris[position])
    }

    override fun getItemCount(): Int = uris.size
}
