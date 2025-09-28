package com.example.photoaivideo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        val btnStartGeneration: Button = findViewById(R.id.btnStartGeneration)
        val seekSimilarity: SeekBar = findViewById(R.id.seekSimilarity)
        val etSimilarity: EditText = findViewById(R.id.etSimilarity)
        val btnSelectReference: Button = findViewById(R.id.btnSelectReference)
        val ivReference: ImageView = findViewById(R.id.ivReference)

        // --- Sync SeekBar and EditText ---
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
        })

        // --- Select Reference Image (optional) ---
        btnSelectReference.text = "Select Reference Image (Optional)"
        btnSelectReference.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        // --- Start Generation ---
        btnStartGeneration.setOnClickListener {
            val prompt = findViewById<EditText>(R.id.etPrompts).text.toString()
            val negativePrompt = findViewById<EditText>(R.id.etNegativePrompts)?.text?.toString()
            val similarity = seekSimilarity.progress
            val seed = findViewById<EditText>(R.id.etSeed)?.text?.toString()?.toLongOrNull()

            val spinnerSize = findViewById<Spinner>(R.id.spinnerSize)
            val size = spinnerSize.selectedItem.toString().split("x")
            val width = size[0].trim().toInt()
            val height = size[1].trim().toInt()

            val spinnerQuality = findViewById<Spinner>(R.id.spinnerQuality)
            val quality = spinnerQuality.selectedItem.toString()

            val spinnerBatch = findViewById<Spinner>(R.id.spinnerBatchSize)
            val batchSize = spinnerBatch.selectedItem.toString().toInt()

            val request = GenerationRequest(
                prompt = prompt,
                negativePrompt = negativePrompt,
                similarity = similarity,
                seed = seed,
                width = width,
                height = height,
                quality = quality,
                batchSize = batchSize,
                referenceImageUri = selectedImageUri?.toString()
            )

            val intent = Intent(this, GeneratedImageResultsActivity::class.java)
            intent.putExtra("generationRequest", request as java.io.Serializable)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            val ivReference: ImageView = findViewById(R.id.ivReference)
            ivReference.setImageURI(selectedImageUri)
        }
    }
}
