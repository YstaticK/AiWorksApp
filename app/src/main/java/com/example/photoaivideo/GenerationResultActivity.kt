package com.example.photoaivideo

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GenerationResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generation_result)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val imageView: ImageView = findViewById(R.id.generatedImageView)

        val btnSave: Button = findViewById(R.id.btnSave)
        val btnBackGenerate: Button = findViewById(R.id.btnBackGenerate)
        val btnBackMain: Button = findViewById(R.id.btnBackMain)

        progressBar.progress = 50

        btnSave.setOnClickListener {
            // TODO: show folder selection
        }

        btnBackGenerate.setOnClickListener {
            finish()
        }

        btnBackMain.setOnClickListener {
            finishAffinity()
        }
    }
}
