package com.example.photoaivideo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderImagesAdapter(
    private val context: Context,
    private val images: List<File>
) : RecyclerView.Adapter<FolderImagesAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageFile = images[position]
        val uri = Uri.fromFile(imageFile)

        holder.imageView.setImageURI(uri)

        // Open fullscreen activity when clicked
        holder.itemView.setOnClickListener {
            val intent = Intent(context, FullscreenImageActivity::class.java)
            intent.putExtra("imageUri", uri.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = images.size
}
