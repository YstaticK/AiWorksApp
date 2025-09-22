package com.example.photoaivideo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.VideoView

class GeneratedVideoAdapter(
    private val ctx: Context,
    private val uris: List<String>
) : BaseAdapter() {

    override fun getCount(): Int = uris.size
    override fun getItem(position: Int): Any = uris[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(ctx).inflate(R.layout.item_generated_video, parent, false)
        val videoView = view.findViewById<VideoView>(R.id.videoGenerated)
        val overlay = view.findViewById<TextView>(R.id.txtVideoOverlay)

        // Placeholder only for now
        overlay.text = uris[position]
        return view
    }
}
