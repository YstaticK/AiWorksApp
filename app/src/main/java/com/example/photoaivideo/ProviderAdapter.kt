package com.example.photoaivideo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class ProviderAdapter(
    private val context: Context,
    private val providers: List<Provider>
) : BaseAdapter() {

    override fun getCount(): Int = providers.size
    override fun getItem(position: Int): Any = providers[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val provider = providers[position]
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(android.R.layout.simple_list_item_2, parent, false)

        val title = view.findViewById<TextView>(android.R.id.text1)
        val subtitle = view.findViewById<TextView>(android.R.id.text2)

        // Provider name
        title.text = provider.name

        // Models joined into a single line
        val models = if (provider.models.isNotEmpty()) provider.models.joinToString(", ") else "No models"
        subtitle.text = if (!provider.apiKey.isNullOrEmpty()) "$models 🔑" else models

        return view
    }
}
