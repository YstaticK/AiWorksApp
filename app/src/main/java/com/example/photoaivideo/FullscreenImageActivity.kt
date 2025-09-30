package com.example.photoaivideo

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Button
import java.io.File

class FullscreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val imageView: ImageView = findViewById(R.id.ivFullscreen)
        val btnInfo: FloatingActionButton = findViewById(R.id.btnImageInfo)

        val uriString = intent.getStringExtra("imageUri")
        var infoText = "No info available"

        if (!uriString.isNullOrEmpty()) {
            val uri = Uri.parse(uriString)
            imageView.setImageURI(uri)

            val file = File(uri.path ?: "")
            if (file.exists()) {
                // Get resolution
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(file.absolutePath, options)
                val width = options.outWidth
                val height = options.outHeight

                val resolutionPart = if (width > 0 && height > 0) {
                    val ratio = simplifyAspectRatio(width, height)
                    "Resolution: ${width}x${height}\nAspect Ratio: $ratio"
                } else {
                    "Resolution info unavailable"
                }

                // Get file size
                val sizeBytes = file.length()
                val sizePart = "File Size: ${formatFileSize(sizeBytes)}"

                infoText = "$resolutionPart\n$sizePart"
            } else {
                infoText = "File not found"
            }
        }

        btnInfo.setOnClickListener {
            showInfoBottomSheet(infoText)
        }
    }

    private fun simplifyAspectRatio(width: Int, height: Int): String {
        fun gcd(a: Int, b: Int): Int {
            return if (b == 0) a else gcd(b, a % b)
        }
        val g = gcd(width, height)
        return "${width / g}:${height / g}"
    }

    private fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        return if (kb < 1024) {
            String.format("%.1f KB", kb)
        } else {
            String.format("%.2f MB", kb / 1024.0)
        }
    }

    private fun showInfoBottomSheet(info: String) {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_image_info, null)
        val tvInfo: TextView = view.findViewById(R.id.tvImageInfo)
        val btnClose: Button = view.findViewById(R.id.btnCloseInfo)

        tvInfo.text = info
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(view)
        dialog.setCancelable(false) // disable swipe-down dismiss
        dialog.show()
    }
}
