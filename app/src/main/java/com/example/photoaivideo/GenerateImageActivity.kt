package com.example.photoaivideo

import android.widget.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory

class GenerateImageActivity : AppCompatActivity() {
    private var selectedReferenceImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        val btnStartGeneration: Button = findViewById(R.id.btnStartGeneration)
        val seekSimilarity: SeekBar = findViewById(R.id.seekSimilarity)
        val etSimilarity: EditText = findViewById(R.id.etSimilarity)
        val btnSelectReference: Button = findViewById(R.id.btnSelectReference)
        val ivReference: ImageView = findViewById(R.id.ivReference)

        val etPrompts: EditText = findViewById(R.id.etPrompts)
        val etNegativePrompts: EditText = findViewById(R.id.etNegativePrompts)
        val etSeed: EditText = findViewById(R.id.etSeed)

        val spinnerSize: Spinner = findViewById(R.id.spinnerSize)
        val spinnerBatchSize: Spinner = findViewById(R.id.spinnerBatchSize)
        val spinnerModel: Spinner = findViewById(R.id.spinnerModel)

        // Fixed LocalSD model
        val modelAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("stable-diffusion")
        )
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModel.adapter = modelAdapter
        spinnerModel.setSelection(0)

        // Fixed sizes (commonly supported)
        val allowedSizes = listOf("512x512", "768x768", "1024x1024")
        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allowedSizes)
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSize.adapter = sizeAdapter
        spinnerSize.setSelection(0)

        // Batch sizes
        val batchSizes = listOf("1", "2", "4", "8")
        val batchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, batchSizes)
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBatchSize.adapter = batchAdapter
        spinnerBatchSize.setSelection(0)

        // Sync SeekBar and EditText
        seekSimilarity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                etSimilarity.setText("$progress%")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        etSimilarity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().replace("%", "").toIntOrNull()
                if (value != null && value in 0..100) {
                    seekSimilarity.progress = value
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Select reference image
        btnSelectReference.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        // Start Generation
        btnStartGeneration.setOnClickListener {
            val sizeSel = spinnerSize.selectedItem?.toString() ?: "512x512"
            val (w, h) = sizeSel.split("x").map { it.toInt() }

            val request = GenerationRequest(
                provider = "LocalSD",
                model = spinnerModel.selectedItem.toString(),
                prompts = etPrompts.text.toString(),
                negativePrompt = etNegativePrompts.text.toString(),
                similarity = seekSimilarity.progress,
                seed = etSeed.text.toString().takeIf { it.isNotBlank() },
                width = w,
                height = h,
                quality = "standard",
                batchSize = spinnerBatchSize.selectedItem.toString().toInt(),
                referenceImageUri = selectedReferenceImageUri?.toString()
            )

            Toast.makeText(this, "Starting image generation…", Toast.LENGTH_SHORT).show()
            try {
                val intent = Intent(this, GeneratedImageResultsActivity::class.java)
                intent.putExtra("generationRequest", request)
                startActivity(intent)
            } catch (e: Exception) {
                ErrorUtils.showErrorDialog(
                    this,
                    "Failed to start results screen.\n\n${e::class.java.simpleName}: ${e.message}"
                )
            }
        }
    }

    // Safe image preview with downsampling
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            selectedReferenceImageUri = data.data
            val ivReference: ImageView = findViewById(R.id.ivReference)

            try {
                val inputStream = contentResolver.openInputStream(selectedReferenceImageUri!!)
                inputStream?.use {
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(it, null, options)

                    val maxDim = 2048
                    var sampleSize = 1
                    while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
                        sampleSize *= 2
                    }

                    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                    val inputStream2 = contentResolver.openInputStream(selectedReferenceImageUri!!)
                    inputStream2?.use { stream2 ->
                        val bitmap = BitmapFactory.decodeStream(stream2, null, decodeOptions)
                        ivReference.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ivReference.setImageURI(selectedReferenceImageUri) // fallback
            }
        }
    }
}
