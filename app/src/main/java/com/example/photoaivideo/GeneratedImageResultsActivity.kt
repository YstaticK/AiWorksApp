package com.example.photoaivideo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.graphics.PorterDuff
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call

class GeneratedImageResultsActivity : AppCompatActivity() {

    private val CHANNEL_ID = "generation_status_channel"
    private var ongoingCall: Call? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_image_results)

        progressBar = findViewById(R.id.progressBarGeneration)
        btnCancel = findViewById(R.id.btnCancelGeneration)
        val recyclerView: RecyclerView = findViewById(R.id.gridGeneratedImages)

        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        btnCancel.visibility = View.VISIBLE
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val request: GenerationRequest = try {
            (intent.getSerializableExtra("generationRequest") as? GenerationRequest)
                ?: throw IllegalArgumentException("generationRequest extra missing")
        } catch (e: Exception) {
            ErrorUtils.showErrorDialog(
                this,
                "Failed to read request.\n\n${e::class.java.simpleName}: ${e.message}"
            )
            return
        }

        createNotificationChannel()

        val service = ProviderAIService(this)
        ongoingCall = service.generateImage(
            provider = request.provider,
            model = request.model,
            prompt = request.prompts,
            width = request.width,
            height = request.height,
            n = request.batchSize,
            referenceImageUri = request.referenceImageUri
        ) { files, error ->
            runOnUiThread {
                btnCancel.visibility = View.GONE
                progressBar.isIndeterminate = false
                progressBar.progress = 100

                if (!error.isNullOrBlank()) {
                    progressBar.progressDrawable.setColorFilter(
                        ContextCompat.getColor(this, android.R.color.holo_red_light),
                        PorterDuff.Mode.SRC_IN
                    )
                    ErrorUtils.showErrorDialog(this, "Image generation failed:\n\n$error")
                    showNotification("Generation Failed", error, success = false)
                    return@runOnUiThread
                }

                if (files != null && files.isNotEmpty()) {
                    recyclerView.adapter = GeneratedImageAdapter(this, files, request)
                    progressBar.progressDrawable.setColorFilter(
                        ContextCompat.getColor(this, android.R.color.holo_green_light),
                        PorterDuff.Mode.SRC_IN
                    )
                    showNotification(
                        "AI Image Generation Finished",
                        "Your images were saved in the library.",
                        success = true
                    )
                } else {
                    val msg = "Image generation did not return any files."
                    progressBar.progressDrawable.setColorFilter(
                        ContextCompat.getColor(this, android.R.color.holo_red_light),
                        PorterDuff.Mode.SRC_IN
                    )
                    ErrorUtils.showErrorDialog(this, msg)
                    showNotification("Generation Failed", msg, success = false)
                }
            }
        }

        btnCancel.setOnClickListener {
            ongoingCall?.cancel()
            progressBar.isIndeterminate = false
            progressBar.progress = 0
            progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_red_light),
                PorterDuff.Mode.SRC_IN
            )
            ErrorUtils.showErrorDialog(this, "Generation canceled by user.")
            btnCancel.visibility = View.GONE
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
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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
