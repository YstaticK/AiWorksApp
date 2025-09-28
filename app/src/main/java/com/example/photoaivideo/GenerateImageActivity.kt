package com.example.photoaivideo
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

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
        val spinnerQuality: Spinner = findViewById(R.id.spinnerQuality)
        val spinnerBatchSize: Spinner = findViewById(R.id.spinnerBatchSize)
        val spinnerProvider: Spinner = findViewById(R.id.spinnerProvider)
        val spinnerModel: Spinner = findViewById(R.id.spinnerModel)

        // Set default provider and models
        spinnerProvider.setSelection(0) // OpenAI
        val defaultAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.models_openai,
            android.R.layout.simple_spinner_item
        )
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModel.adapter = defaultAdapter
        spinnerModel.setSelection(0) // DALL·E 2

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
                val value = s.toString().replace("%","").toIntOrNull()
                if (value != null && value in 0..100) {
                    seekSimilarity.progress = value
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        val providerModels = mapOf(
            "OpenAI" to R.array.models_openai,
            "Stability AI" to R.array.models_stability,
            "Runway" to R.array.models_runway
        )

        // Start Generation
        // --- Provider -> Model cascade ---

        fun updateModelSpinner(selected: String?) {
            val resId = providerModels[selected] ?: R.array.image_models_openai
            val modelAdapter = ArrayAdapter.createFromResource(
                this,
                resId,
                android.R.layout.simple_spinner_item
            )
            modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerModel.adapter = modelAdapter
        }

        spinnerProvider.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        spinnerProvider.setSelection(0)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateModelSpinner(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // initialize models for current provider
        updateModelSpinner(spinnerProvider.selectedItem?.toString())

        btnStartGeneration.setOnClickListener {
            val request = GenerationRequest(
                provider = spinnerProvider.selectedItem.toString(),
                model = spinnerModel.selectedItem.toString(),



                prompts = etPrompts.text.toString(),
                negativePrompt = etNegativePrompts.text.toString(),
                similarity = seekSimilarity.progress,
                seed = etSeed.text.toString().takeIf { it.isNotBlank() },
                width = when (spinnerSize.selectedItem.toString()) {
                    "512x512" -> 512
                    "768x768" -> 768
                    "1024x1024" -> 1024
                    else -> 512
                },
                height = when (spinnerSize.selectedItem.toString()) {
                    "512x512" -> 512
                    "768x768" -> 768
                    "1024x1024" -> 1024
                    else -> 512
                },
                quality = spinnerQuality.selectedItem.toString(),
                batchSize = spinnerBatchSize.selectedItem.toString().toInt(),
                referenceImageUri = selectedReferenceImageUri?.toString()
            )

            val intent = Intent(this, GeneratedImageResultsActivity::class.java)
            intent.putExtra("generationRequest", request)
            startActivity(intent)
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
