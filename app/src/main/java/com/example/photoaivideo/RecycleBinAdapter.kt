package com.example.photoaivideo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class RecycleBinAdapter(
    private val context: Context,
    private val files: List<File>,
    private val onRestore: (File) -> Unit,
    private val onDelete: (File) -> Unit
) : RecyclerView.Adapter<RecycleBinAdapter.BinViewHolder>() {

    inner class BinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.textDeletedItemName)
        val btnRestore: Button = view.findViewById(R.id.btnRestoreItem)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycle_bin, parent, false)
        return BinViewHolder(view)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        val file = files[position]
        holder.textName.text = file.name

        holder.btnRestore.setOnClickListener { onRestore(file) }
        holder.btnDelete.setOnClickListener { onDelete(file) }
    }

    override fun getItemCount(): Int = files.size
}
