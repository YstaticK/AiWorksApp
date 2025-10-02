package com.example.photoaivideo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ModelsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        val listView: ListView = findViewById(R.id.listViewModels)

        // Local-only provider
        val providers = listOf(
            Provider("LocalSD", null, mutableListOf("stable-diffusion"), "http://192.168.178.27:7860")
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            providers.map { "${it.name} - ${it.models.joinToString()}" }
        )
        listView.adapter = adapter

        Toast.makeText(this, "Using Local Stable Diffusion only", Toast.LENGTH_LONG).show()
    }
}
