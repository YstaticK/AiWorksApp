package com.example.aiworks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // simple UI just to confirm launch works
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val text = TextView(this@MainActivity).apply {
                text = "Hello from AI Works!"
                textSize = 24f
                setPadding(40, 200, 40, 40)
            }
            addView(text)
        }

        setContentView(layout)
    }
}
