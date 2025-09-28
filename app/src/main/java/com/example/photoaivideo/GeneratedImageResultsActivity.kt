package com.example.photoaivideo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.graphics.PorterDuff
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GeneratedImageResultsActivity : AppCompatActivity() {

    private val CHANNEL_ID = "generation_status_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        val progressBar: ProgressBar = findViewById(R.id.progressBarGeneration)
        progressBar.max = 100
        progressBar.visibility = View.VISIBLE

        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", null)

        if (request == null) {
            Toast.makeText(this, "No generation request found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (apiKey.isNullOrBlank()) {
            Toast.makeText(this, "Missing API key. Please enter it on the previous screen.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        createNotificationChannel()

        // Fake loading animation while waiting
        Thread {
            for (i in 1..100) {
                Thread.sleep(40)
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

        val service = OpenAIService(this, apiKey)
        service.generateImage(
            prompt = request.prompts,
            width = request.width,
            height = request.height,
            n = request.batchSize
        ) { files ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (files != null && files.isNotEmpty()) {
                    recyclerView.adapter = GeneratedImageAdapter(this, files, request)
                    showNotification(
                        "AI Image Generation Finished",
                        "Your images were saved in the library.",
                        success = true
                    )
                } else {
                    Toast.makeText(this, "Image generation failed.", Toast.LENGTH_LONG).show()
                    showNotification(
                        "Generation Failed",
                        "Something went wrong. Tap to retry.",
                        success = false
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Generation Status"
            val descriptionText = "Notifies when image generation is complete"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String, success: Boolean) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val intent = if (success) {
            Intent(this, ImagesLibraryActivity::class.java)
        } else {
            Intent(this, GenerateImageActivity::class.java)
        }.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        builder.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(1001, builder.build())
        }
    }
}
