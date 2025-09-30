package com.example.photoaivideo

import android.widget.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {

    private var selectedReferenceImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        val apiKeyInput: EditText = findViewById(R.id.etApiKey)
        val cbRemember: CheckBox = findViewById(R.id.cbRememberKey)
        prefs.getString("api_key", "")?.let {
            if (it.isNotEmpty()) {
                apiKeyInput.setText(it)
                cbRemember.isChecked = true
            }
        }

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

        // Default provider + models
        spinnerProvider.setSelection(0)
        val defaultAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.models_openai,
            android.R.layout.simple_spinner_item
        )
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModel.adapter = defaultAdapter
        spinnerModel.setSelection(0)

        // Aspect Ratios
        val aspectRatios = listOf("1:1", "16:9", "4:3")
        spinnerAspectRatio.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            aspectRatios
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Sizes mapped to aspect ratios
        val sizesByRatio = mapOf(
            "1:1" to listOf("512x512", "768x768", "1024x1024"),
            "16:9" to listOf("1280x720", "1920x1080"),
            "4:3" to listOf("800x600", "1024x768")
        )

        // Update sizes when aspect ratio changes
        spinnerAspectRatio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val ratio = aspectRatios[position]
                val sizes = sizesByRatio[ratio] ?: listOf("512x512")
                spinnerSize.adapter = ArrayAdapter(
                    this@GenerateImageActivity,
                    android.R.layout.simple_spinner_item,
                    sizes
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerAspectRatio.setSelection(0) // default: 1:1 → loads 512x512

        // Batch sizes
        val batchSizes = listOf("1", "2", "4", "8", "10")
        spinnerBatchSize.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            batchSizes
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
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
            val apiKey = apiKeyInput.text.toString().trim()
            if (apiKey.isEmpty()) {
                ErrorUtils.showErrorDialog(this, "Please enter your OpenAI API key")
                return@setOnClickListener
            }

            if (cbRemember.isChecked) {
                prefs.edit().putString("api_key", apiKey).apply()
            } else {
                prefs.edit().remove("api_key").apply()
            }

            val sizeSel = spinnerSize.selectedItem?.toString() ?: "512x512"
            val (w, h) = sizeSel.split("x").map { it.toInt() }.let { it[0] to it[1] }

            val request = GenerationRequest(
                provider = spinnerProvider.selectedItem.toString(),
                model = spinnerModel.selectedItem.toString(),
                prompts = etPrompts.text.toString(),
                negativePrompt = etNegativePrompts.text.toString(),
                similarity = seekSimilarity.progress,
                seed = etSeed.text.toString().takeIf { it.isNotBlank() },
                width = w,
                height = h,
                quality = "standard", // placeholder
                batchSize = spinnerBatchSize.selectedItem.toString().toInt(),
                referenceImageUri = selectedReferenceImageUri?.toString()
            )

            Toast.makeText(this, "Starting image generation…", Toast.LENGTH_SHORT).show()

            try {
                val intent = Intent(this, GeneratedImageResultsActivity::class.java)
                intent.putExtra("generationRequest", request)
                intent.putExtra("apiKey", apiKey)
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
            ivReference.setImageURI(selectedReferenceImageUri)
        }
    }
}
