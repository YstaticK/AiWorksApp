package com.example.photoaivideo

import android.os.Bundle
import android.text.EditWatcher
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class ModelsActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val providers = mutableListOf<Provider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        listView = findViewById(R.id.listViewModels)
        val btnAddProvider: Button = findViewById(R.id.btnAddModel)

        // Load providers
        providers.clear()
        providers.addAll(ProviderRegistry.loadAll(this))

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            providers.map { it.name }.toMutableList()
        )
        listView.adapter = adapter

        btnAddProvider.text = "Add Provider"
        btnAddProvider.setOnClickListener {
            showAddProviderDialog()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            showEditProviderDialog(position)
        }
    }

    private fun refreshList() {
        adapter.clear()
        adapter.addAll(providers.map { it.name })
        adapter.notifyDataSetChanged()
    }

    private fun showAddProviderDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputName = AutoCompleteTextView(this).apply {
            hint = "Provider name"
            val knownNames = ProviderRegistry.knownDefaults.map { it.name }
            setAdapter(ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, knownNames))
        }
        val inputKey = EditText(this).apply {
            hint = "API Key (optional)"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val inputBaseUrl = EditText(this).apply {
            hint = "Base URL (optional)"
        }

        layout.addView(inputName)
        layout.addView(inputKey)
        layout.addView(inputBaseUrl)

        // Autofill baseUrl + models when known provider name entered
        inputName.setOnItemClickListener { _, _, pos, _ ->
            val name = inputName.adapter.getItem(pos) as String
            val default = ProviderRegistry.knownDefaults.find { it.name == name }
            if (default != null) {
                inputBaseUrl.setText(default.baseUrl ?: "")
                Toast.makeText(this, "Autofilled defaults for $name", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Add New Provider")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = inputName.text.toString().trim()
                val key = inputKey.text.toString().trim()
                val baseUrlInput = inputBaseUrl.text.toString().trim()

                if (name.isNotEmpty()) {
                    val default = ProviderRegistry.knownDefaults.find { it.name == name }
                    val newProvider = if (default != null) {
                        Provider(
                            default.name,
                            if (key.isNotEmpty()) key else null,
                            default.models.toMutableList(),
                            default.baseUrl
                        )
                    } else {
                        Provider(
                            name,
                            if (key.isNotEmpty()) key else null,
                            mutableListOf(),
                            if (baseUrlInput.isNotEmpty()) baseUrlInput else null
                        )
                    }

                    providers.add(newProvider)
                    ProviderRegistry.saveAll(this, providers)
                    refreshList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditProviderDialog(index: Int) {
        val provider = providers[index]

        val layout = ScrollView(this).apply {
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 40, 50, 10)
            }
            addView(container)

            val inputName = EditText(context).apply {
                hint = "Provider name"
                setText(provider.name)
            }
            val inputKey = EditText(context).apply {
                hint = "API Key"
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                setText(provider.apiKey ?: "")
            }
            val inputBaseUrl = EditText(context).apply {
                hint = "Base URL"
                setText(provider.baseUrl ?: "")
            }

            container.addView(inputName)
            container.addView(inputKey)
            container.addView(inputBaseUrl)

            // Existing models with delete buttons
            val modelsLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }
            provider.models.forEachIndexed { i, modelName ->
                val row = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                }
                val tv = TextView(context).apply {
                    text = modelName
                    layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                val btnDelete = Button(context).apply {
                    text = "Delete"
                    setOnClickListener {
                        provider.models.removeAt(i)
                        ProviderRegistry.saveAll(this@ModelsActivity, providers)
                        showEditProviderDialog(index) // reopen to refresh
                    }
                }
                row.addView(tv)
                row.addView(btnDelete)
                modelsLayout.addView(row)
            }
            container.addView(modelsLayout)

            // Add new model
            val inputModel = EditText(context).apply { hint = "Add new model" }
            container.addView(inputModel)

            AlertDialog.Builder(this@ModelsActivity)
                .setTitle("Edit Provider")
                .setView(this)
                .setPositiveButton("Save") { _, _ ->
                    provider.name = inputName.text.toString().trim()
                    provider.apiKey = inputKey.text.toString().trim().ifEmpty { null }
                    provider.baseUrl = inputBaseUrl.text.toString().trim().ifEmpty { null }

                    val modelName = inputModel.text.toString().trim()
                    if (modelName.isNotEmpty()) {
                        provider.models.add(modelName)
                    }

                    ProviderRegistry.saveAll(this@ModelsActivity, providers)
                    refreshList()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
