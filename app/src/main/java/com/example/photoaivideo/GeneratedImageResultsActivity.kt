package com.example.photoaivideo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import kotlinx.coroutines.*
import java.io.InputStream
import android.util.Base64

class GeneratedImageResultsActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Generation Result"

        val scrollView = ScrollView(this)
        val container = TextView(this)
        scrollView.addView(container)
        container.setPadding(24)
        setContentView(scrollView)

        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        if (request == null) {
            container.text = "No generation request provided."
            return
        }

        container.text = "Generating image...\nPlease wait..."

        scope.launch {
            val bitmap = if (request.referenceImageUri.isNotEmpty()) {
                // img2img
                val uri = Uri.parse(request.referenceImageUri)
                val base64Img = getImageBase64(uri)
                StableDiffusionClient.img2img(request, base64Img)
            } else {
                // txt2img
                StableDiffusionClient.txt2img(request)
            }

            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    val imgView = ImageView(this@GeneratedImageResultsActivity)
                    imgView.setImageBitmap(bitmap)
                    scrollView.addView(imgView)
                    container.text = ""
                } else {
                    container.text = "Failed to generate image."
                }
            }
        }
    }

    private fun getImageBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return ""
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
