package com.example.photoaivideo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ModelsActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var models = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        listView = findViewById(R.id.listViewModels)
        val fab: FloatingActionButton = findViewById(R.id.btnAddModel)

        // Load saved models
        models = ModelStorage.loadModels(this)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, models)
        listView.adapter = adapter

        // Add new model
        fab.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter model name"
            AlertDialog.Builder(this)
                .setTitle("Add Model")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val name = input.text.toString().trim()
                    if (name.isNotEmpty()) {
                        models.add(name)
                        ModelStorage.saveModels(this, models)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Model added", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Delete model on long click
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val model = models[position]
            AlertDialog.Builder(this)
                .setTitle("Delete Model")
                .setMessage("Remove '$model'?")
                .setPositiveButton("Delete") { _, _ ->
                    models.removeAt(position)
                    ModelStorage.saveModels(this, models)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Model deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }
    }
}
