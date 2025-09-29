package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class ReferenceLibraryActivity : BasePermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference_library)

        if (!PermissionsHelper.hasStoragePermission(this)) {
            PermissionsHelper.requestStoragePermission(this)
        } else {
            onStoragePermissionGranted()
        }
    }

    override fun onStoragePermissionGranted() {
        val btnImages = findViewById<Button>(R.id.btnReferenceImages)
        val btnVideos = findViewById<Button>(R.id.btnReferenceVideos)
        val btnUrls = findViewById<Button>(R.id.btnReferenceUrls)

        btnImages.setOnClickListener {
            startActivity(Intent(this, ReferenceImagesActivity::class.java))
        }
        btnVideos.setOnClickListener {
            startActivity(Intent(this, ReferenceVideosActivity::class.java))
        }
        btnUrls.setOnClickListener {
            startActivity(Intent(this, ReferenceUrlsActivity::class.java))
        }
    }

    override fun onStoragePermissionDenied() {
        Toast.makeText(
            this,
            "Storage permission required to access the reference library.",
            Toast.LENGTH_LONG
        ).show()
        PermissionsHelper.requestStoragePermission(this)
    }
}
