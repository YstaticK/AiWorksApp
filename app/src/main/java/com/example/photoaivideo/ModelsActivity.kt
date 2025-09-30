package com.example.photoaivideo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ModelsActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val models = mutableListOf<Model>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        listView = findViewById(R.id.listViewModels)
        val btnAddModel: Button = findViewById(R.id.btnAddModel)

        models.clear()
        models.addAll(ModelStorage.loadModels(this))
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, models.map { "${it.provider}: ${it.name}" })
        listView.adapter = adapter

        btnAddModel.setOnClickListener {
            showAddModelDialog()
        }
    }

    private fun showAddModelDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputProvider = EditText(this).apply { hint = "Provider" }
        val inputName = EditText(this).apply { hint = "Model name" }
        layout.addView(inputProvider)
        layout.addView(inputName)

        AlertDialog.Builder(this)
            .setTitle("Add New Model")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val provider = inputProvider.text.toString().trim()
                val name = inputName.text.toString().trim()
                if (provider.isNotEmpty() && name.isNotEmpty()) {
                    val newModel = Model(provider, name)
                    models.add(newModel)
                    ModelStorage.saveModels(this, models)
                    adapter.clear()
                    adapter.addAll(models.map { "${it.provider}: ${it.name}" })
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
