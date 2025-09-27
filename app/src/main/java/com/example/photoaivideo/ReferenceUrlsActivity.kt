package com.example.photoaivideo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class ReferenceUrlsActivity : AppCompatActivity() {

    private lateinit var urlsContainer: LinearLayout
    private val prefs by lazy { getSharedPreferences("reference_urls", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_urls)

        urlsContainer = findViewById(R.id.urlsContainer)
        val btnAddUrl = findViewById<Button>(R.id.btnAddUrl)

        // Load saved URLs
        val savedUrls = prefs.getStringSet("urls", emptySet()) ?: emptySet()
        for (url in savedUrls) {
            addUrlTextbox(url)
        }

        btnAddUrl.setOnClickListener {
            addUrlTextbox("")
        }
    }

    private fun addUrlTextbox(initialText: String) {
        val editText = EditText(this)
        editText.setText(initialText)
        editText.hint = "Enter URL"
        urlsContainer.addView(editText)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                saveUrls()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun saveUrls() {
        val urls = mutableSetOf<String>()
        for (i in 0 until urlsContainer.childCount) {
            val child = urlsContainer.getChildAt(i) as? EditText
            val text = child?.text?.toString()?.trim()
            if (!text.isNullOrEmpty()) {
                urls.add(text)
            }
        }
        prefs.edit().putStringSet("urls", urls).apply()
    }
}
