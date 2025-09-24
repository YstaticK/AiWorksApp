package com.example.photoaivideo



import android.os.Bundle

import android.widget.Button

import android.widget.Spinner

import android.widget.CheckBox

import android.widget.EditText

import android.view.View

import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val cbSaveReference = findViewById<CheckBox>(R.id.cbSaveReference)
        val etReferenceName = findViewById<EditText>(R.id.etReferenceName)
        val spinnerReferenceDestination = findViewById<Spinner>(R.id.spinnerReferenceDestination)

        cbSaveReference.setOnCheckedChangeListener { _, isChecked ->
            etReferenceName.visibility = if (isChecked) View.VISIBLE else View.GONE
            spinnerReferenceDestination.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)
        Toast.makeText(this, "GenerateImageActivity opened", Toast.LENGTH_SHORT).show()

        // Hook up spinner (model selection)
        val spinnerModel: Spinner = findViewById(R.id.spinnerModelImage)

        // Hook up button (safe for now, no crash)
        val btnGenerateImage: Button = findViewById(R.id.btnStartGeneration)
        btnGenerateImage.setOnClickListener {
            // TODO: Add image generation logic
        }
    }
}
