package com.example.photoaivideo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import kotlinx.coroutines.*
import android.util.Base64
import java.io.InputStream

class GeneratedImageResultsActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Generation Result"

        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24)
        }
        scrollView.addView(layout)
        setContentView(scrollView)

        val request = intent.getSerializableExtra("generationRequest") as? GenerationRequest
        if (request == null) {
            Toast.makeText(this, "No generation request provided", Toast.LENGTH_LONG).show()
            return
        }

        val statusText = TextView(this).apply {
            text = "Generating image...\nPlease wait..."
        }
        layout.addView(statusText)

        scope.launch {
            val response = if (request.referenceImageUri.isNotEmpty()) {
                val uri = Uri.parse(request.referenceImageUri)
                val base64 = getImageBase64(uri)
                StableDiffusionClient.img2img(request, base64)
            } else {
                StableDiffusionClient.txt2img(request)
            }

            withContext(Dispatchers.Main) {
                if (response.error != null) {
                    showErrorPanel(layout, response.error!!)
                    Toast.makeText(this@GeneratedImageResultsActivity, "Generation failed. See details below.", Toast.LENGTH_LONG).show()
                } else if (response.bitmap != null) {
                    val imageView = ImageView(this@GeneratedImageResultsActivity)
                    imageView.setImageBitmap(response.bitmap)
                    layout.removeAllViews()
                    layout.addView(imageView)
                } else {
                    showErrorPanel(layout, "Unknown error, no bitmap or error returned.")
                }
            }
        }
    }

    private fun getImageBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return ""
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun showErrorPanel(parent: LinearLayout, message: String) {
        parent.removeAllViews()

        val errorTitle = TextView(this).apply {
            text = "⚠️ Error:"
            textSize = 18f
            setPadding(0, 0, 0, 8)
        }
        val errorBox = TextView(this).apply {
            text = message
            setPadding(16)
            isVerticalScrollBarEnabled = true
        }
        val copyButton = Button(this).apply {
            text = "📋 Copy Error"
            setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Error", message))
                Toast.makeText(this@GeneratedImageResultsActivity, "Error copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }

        parent.addView(errorTitle)
        parent.addView(errorBox)
        parent.addView(copyButton)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
