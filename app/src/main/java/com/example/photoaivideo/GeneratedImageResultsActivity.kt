package com.example.photoaivideo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.graphics.PorterDuff
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class GeneratedImageResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        // Progress bar setup
        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)
        progressBar.max = 100
        progressBar.visibility = View.VISIBLE

        Thread {
            for (i in 1..100) {
                Thread.sleep(50)
                runOnUiThread {
                    progressBar.progress = i
                    if (i == 100) {
                        progressBar.progressDrawable.setColorFilter(
                            ContextCompat.getColor(this, android.R.color.holo_green_light),
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }
        }.start()

        // RecyclerView setup
        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load request if available
        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest

        // Library folder
        val miscDir = File(getExternalFilesDir(null), "misc").apply { mkdirs() }

        // Temporary folder where generated images are written
        val imagesDir = getExternalFilesDir("generated_images")
        val images = imagesDir?.listFiles()?.toList() ?: emptyList()

        // Save images into misc + clean up temporary folder
        images.forEach { file ->
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val destFile = File(miscDir, file.name)
                FileOutputStream(destFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                }
                file.delete() // clean up temp file after saving
            }
        }

        // Refresh the list from misc
        val savedImages = miscDir.listFiles()?.toList() ?: emptyList()

        if (request != null) {
            recyclerView.adapter = GeneratedImageAdapter(this, savedImages, request)
        }
    }
}
