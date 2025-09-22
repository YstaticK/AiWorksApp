package com.example.photoaivideo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class GeneratedImageAdapter(
    private val ctx: Context,
    private val uris: List<String>
) : BaseAdapter() {

    override fun getCount(): Int = uris.size
    override fun getItem(position: Int): Any = uris[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(ctx).inflate(R.layout.item_generated_image, parent, false)
        val img = view.findViewById<ImageView>(R.id.imgGenerated)
        // For now just show placeholder color/text
        img.setBackgroundColor(0xFFCCCCCC.toInt())

        val overlay = TextView(ctx)
        overlay.text = uris[position]
        overlay.setTextColor(0xFF000000.toInt())

        return view
    }
}
