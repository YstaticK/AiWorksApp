package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class ReferenceUrlsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_urls)

        val urlsContainer = findViewById<LinearLayout>(R.id.urlsContainer)
        val btnAddUrl = findViewById<Button>(R.id.btnAddUrl)

        btnAddUrl.setOnClickListener {
            val editText = EditText(this)
            editText.hint = "Paste URL here"
            urlsContainer.addView(editText)
        }
    }
}
