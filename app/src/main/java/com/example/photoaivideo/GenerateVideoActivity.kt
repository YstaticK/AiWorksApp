package com.example.photoaivideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GenerateVideoActivity : AppCompatActivity() {

    private lateinit var imgReference: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var videoReference: VideoView
    private lateinit var btnSelectVideo: Button
    private lateinit var chkSaveReferences: CheckBox
    private lateinit var txtPrompt: EditText
    private lateinit var txtNegativePrompt: AutoCompleteTextView
    private lateinit var spinnerLength: Spinner
    private lateinit var spinnerQuality: Spinner
    private lateinit var btnGenerateVideoNow: Button
    private lateinit var progressVideoGeneration: ProgressBar

    private var selectedImageUri: Uri? = null
    private var selectedVideoUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 201
        private const val PICK_VIDEO_REQUEST = 202
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_video)

        imgReference = findViewById(R.id.imgReferenceVideo)
        btnSelectImage = findViewById(R.id.btnSelectImageReference)
        videoReference = findViewById(R.id.videoReference)
        btnSelectVideo = findViewById(R.id.btnSelectVideoReference)
        chkSaveReferences = findViewById(R.id.chkSaveReferences)
        txtPrompt = findViewById(R.id.txtPromptVideo)
        txtNegativePrompt = findViewById(R.id.txtNegativePromptVideo)
        spinnerLength = findViewById(R.id.spinnerLength)
        spinnerQuality = findViewById(R.id.spinnerQualityVideo)
        btnGenerateVideoNow = findViewById(R.id.btnGenerateVideoNow)
        progressVideoGeneration = findViewById(R.id.progressVideoGeneration)

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Reference Image"), PICK_IMAGE_REQUEST)
        }

        btnSelectVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(Intent.createChooser(intent, "Select Reference Video"), PICK_VIDEO_REQUEST)
        }

        btnGenerateVideoNow.setOnClickListener {
            startVideoGeneration()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                    imgReference.setImageURI(selectedImageUri)
                    chkSaveReferences.visibility = CheckBox.VISIBLE
                }
                PICK_VIDEO_REQUEST -> {
                    selectedVideoUri = data?.data
                    videoReference.setVideoURI(selectedVideoUri)
                    videoReference.start()
                    chkSaveReferences.visibility = CheckBox.VISIBLE
                }
            }
        }
    }

    private fun startVideoGeneration() {
        val length = spinnerLength.selectedItem?.toString() ?: "30s"

        progressVideoGeneration.visibility = ProgressBar.VISIBLE
        btnGenerateVideoNow.isEnabled = false

        // fake-generate
        val fakeVideoUris = ArrayList<String>()
        fakeVideoUris.add("generated_video_${length}_${Random.nextInt(1000)}")

        progressVideoGeneration.visibility = ProgressBar.GONE
        btnGenerateVideoNow.isEnabled = true

        val intent = Intent(this, GeneratedVideoResultsActivity::class.java)
        intent.putStringArrayListExtra("generatedVideos", fakeVideoUris)
        startActivity(intent)
    }
}
