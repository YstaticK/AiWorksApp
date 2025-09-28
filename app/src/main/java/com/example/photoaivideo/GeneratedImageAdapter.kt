package com.example.photoaivideo

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GeneratedImageAdapter(
    private val context: Context,
    private val images: MutableList<File?>, // allow placeholders (null)
    private val request: GenerationRequest
) : RecyclerView.Adapter<GeneratedImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivGeneratedImage)
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

            holder.imageView.setOnClickListener {
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("imagePath", file.absolutePath)
                intent.putExtra("generationRequest", request)
                context.startActivity(intent)
            }
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount(): Int = images.size

    // Replace placeholder with actual image
    fun updateImageAt(index: Int, file: File) {
        images[index] = file
        notifyItemChanged(index)
    }
}
