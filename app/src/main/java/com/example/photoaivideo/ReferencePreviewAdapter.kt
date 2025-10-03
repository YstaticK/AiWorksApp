package com.example.photoaivideo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class ReferencePreviewAdapter(
    private val context: Context,
    private val items: List<File>,
    private val onTap: (File) -> Unit
) : RecyclerView.Adapter<ReferencePreviewAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generated_image, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val f = items[position]
        Glide.with(context).load(f).into(holder.image)
        holder.itemView.setOnClickListener { onTap(f) }
    }

    override fun getItemCount(): Int = items.size
}
