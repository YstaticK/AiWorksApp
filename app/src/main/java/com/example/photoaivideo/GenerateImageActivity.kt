package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {

    private lateinit var spinnerModelImage: Spinner
    private lateinit var ivReference: ImageView
    private lateinit var btnGenerateImage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        spinnerModelImage = findViewById(R.id.spinnerModelImage)
        ivReference = findViewById(R.id.ivReference)
        btnGenerateImage = findViewById(R.id.btnGenerateImage)

        // Example spinner values
        val models = listOf("Model A", "Model B", "Model C")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, models)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModelImage.adapter = adapter

        btnGenerateImage.setOnClickListener {
            val intent = Intent(this, GeneratedImageResultsActivity::class.java)
            startActivity(intent)
        }
    }
}
