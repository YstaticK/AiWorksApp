package com.example.photoaivideo

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

class GeneratedVideoAdapter(
    private val videoUris: List<Uri>,
    private val captions: List<String>
) : RecyclerView.Adapter<GeneratedVideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.generatedVideo)
        val captionView: TextView = itemView.findViewById(R.id.generatedVideoCaption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generated_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.videoView.setVideoURI(videoUris[position])
        holder.captionView.text = captions[position]
        holder.videoView.seekTo(100) // preview first frame
    }

    override fun getItemCount(): Int = videoUris.size
}
