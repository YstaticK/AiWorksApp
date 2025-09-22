package com.example.photoaivideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class GenerateReferenceLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        textView.text = "Generate Reference Library Screen"
        textView.textSize = 24f
        setContentView(textView)
    }
}
