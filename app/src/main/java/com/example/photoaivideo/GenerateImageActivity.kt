package com.example.photoaivideo

import android.app.AlertDialog
import android.view.View
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
        val spinnerProvider: Spinner = findViewById(R.id.spinnerProvider)
        val spinnerModel: Spinner = findViewById(R.id.spinnerModel)
        val spinnerAspectRatio: Spinner = findViewById(R.id.spinnerAspectRatio)

        // --- Load providers + models ---
        val providers = ProviderRegistry.loadAll(this)
        val providerModels: Map<String, List<String>> = providers
            .associate { it.name to it.models.sorted() }

        val providerNames = providerModels.keys.sorted()

        val providerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, providerNames)
        providerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProvider.adapter = providerAdapter

        spinnerProvider.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val provider = providerNames[position]
                val models = providerModels[provider] ?: emptyList()
                val modelAdapter = ArrayAdapter(this@GenerateImageActivity, android.R.layout.simple_spinner_item, models)
                modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerModel.adapter = modelAdapter
                if (models.isNotEmpty()) spinnerModel.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (providerNames.isNotEmpty()) spinnerProvider.setSelection(0)

        // --- Aspect ratios ---
        val aspectRatios = listOf("1:1", "16:9", "4:3", "3:2")
        val aspectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, aspectRatios)
        aspectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAspectRatio.adapter = aspectAdapter

        val sizeOptions = mapOf(
            "1:1" to listOf("512x512", "768x768", "1024x1024"),
            "16:9" to listOf("1280x720", "1920x1080"),
            "4:3" to listOf("800x600", "1024x768"),
            "3:2" to listOf("900x600", "1500x1000")
        )

        spinnerAspectRatio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val aspect = aspectRatios[position]
                val sizes = sizeOptions[aspect] ?: listOf("512x512")
                val sizeAdapter = ArrayAdapter(this@GenerateImageActivity, android.R.layout.simple_spinner_item, sizes)
                sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSize.adapter = sizeAdapter
                spinnerSize.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerAspectRatio.setSelection(0)

        // --- Batch sizes ---
        val batchSizes = listOf("1", "2", "4", "8", "10")
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
            val provider = spinnerProvider.selectedItem?.toString() ?: ""
            val apiKey = ProviderRegistry.getApiKey(this, provider)
            val baseUrl = ProviderRegistry.getBaseUrl(this, provider)

            if (apiKey.isNullOrEmpty() || baseUrl.isNullOrEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Missing Provider Setup")
                    .setMessage("Provider $provider is missing API Key or Base URL. Please configure it in Models.")
                    .setPositiveButton("Open Models") { _, _ ->
                        startActivity(Intent(this, ModelsActivity::class.java))
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                return@setOnClickListener
            }

            val sizeSel = spinnerSize.selectedItem?.toString() ?: "512x512"
            val (w, h) = sizeSel.split("x").map { it.toInt() }

            val request = GenerationRequest(
                provider = provider,
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
                intent.putExtra("apiKey", apiKey)
                intent.putExtra("baseUrl", baseUrl)
                startActivity(intent)
            } catch (e: Exception) {
                ErrorUtils.showErrorDialog(
                    this,
                    "Failed to start results screen.\n\n${e::class.java.simpleName}: ${e.message}"
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            selectedReferenceImageUri = data.data
            val ivReference: ImageView = findViewById(R.id.ivReference)

            try {
                val inputStream = contentResolver.openInputStream(selectedReferenceImageUri!!)
                inputStream?.use {
                    // Setup decode options to avoid OOM with large images
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    // First decode bounds
                    BitmapFactory.decodeStream(it, null, options)

                    // Calculate sample size (scale down if >2048px)
                    val maxDim = 2048
                    var sampleSize = 1
                    while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
                        sampleSize *= 2
                    }

                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                    }

                    // Reopen stream for actual decode
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
