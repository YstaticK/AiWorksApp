package com.example.photoaivideo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ModelsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var modelsMap = mutableMapOf<String, MutableList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        listView = findViewById(R.id.listViewModels)
        val btnAddProvider: Button = findViewById(R.id.btnAddProvider)
        val btnAddModel: Button = findViewById(R.id.btnAddModel)

        // Load existing models
        modelsMap = ModelStorage.loadModels(this).toMutableMap()

        refreshList()

        // Add new provider + first model
        btnAddProvider.setOnClickListener {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val padding = (16 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding, padding, padding)
            }

            val inputProvider = EditText(this).apply { hint = "Provider name" }
            val inputModel = EditText(this).apply { hint = "First model name" }
            layout.addView(inputProvider)
            layout.addView(inputModel)

            AlertDialog.Builder(this)
                .setTitle("Add Provider")
                .setView(layout)
                .setPositiveButton("Add") { _, _ ->
                    val provider = inputProvider.text.toString().trim()
                    val model = inputModel.text.toString().trim()
                    if (provider.isNotEmpty() && model.isNotEmpty()) {
                        if (!modelsMap.containsKey(provider)) {
                            modelsMap[provider] = mutableListOf(model)
                        } else {
                            modelsMap[provider]?.add(model)
                        }
                        saveAndRefresh()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Add model to existing provider
        btnAddModel.setOnClickListener {
            if (modelsMap.isEmpty()) {
                Toast.makeText(this, "No providers available. Add a provider first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val providerList = modelsMap.keys.toTypedArray()
            var selectedProvider = providerList.first()

            val providerSpinner = Spinner(this).apply {
                adapter = ArrayAdapter(
                    this@ModelsActivity,
                    android.R.layout.simple_spinner_item,
                    providerList
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedProvider = providerList[position]
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val padding = (16 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding, padding, padding)
            }

            val inputModel = EditText(this).apply { hint = "Model name" }
            layout.addView(providerSpinner)
            layout.addView(inputModel)

            AlertDialog.Builder(this)
                .setTitle("Add Model")
                .setView(layout)
                .setPositiveButton("Add") { _, _ ->
                    val model = inputModel.text.toString().trim()
                    if (model.isNotEmpty()) {
                        modelsMap[selectedProvider]?.add(model)
                        saveAndRefresh()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun refreshList() {
        val items = modelsMap.flatMap { (provider, models) ->
            models.map { "$provider: $it" }
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
    }

    private fun saveAndRefresh() {
        ModelStorage.saveModels(this, modelsMap)
        refreshList()
    }
}
