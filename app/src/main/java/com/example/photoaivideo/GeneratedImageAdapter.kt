package com.example.photoaivideo

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GeneratedImageAdapter(
    private val context: Context,
    private val images: MutableList<File?>,
    private val request: GenerationRequest
) : RecyclerView.Adapter<GeneratedImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivGeneratedImage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.pbLoading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generated_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val file = images[position]
        if (file != null && file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            holder.imageView.setImageBitmap(bitmap)
            holder.progressBar.visibility = View.GONE

            holder.imageView.setOnClickListener {
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("imagePath", file.absolutePath)
                intent.putExtra("generationRequest", request)
                context.startActivity(intent)
            }
        } else {
            holder.imageView.setImageResource(android.R.color.darker_gray)
            holder.progressBar.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = images.size

    fun updateImageAt(index: Int, file: File) {
        images[index] = file
        notifyItemChanged(index)
    }
}
