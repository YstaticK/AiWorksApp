import android.view.View
import android.widget.Spinner
import android.widget.AdapterView
import android.view.View
import android.widget.Spinner
import android.widget.AdapterView
import android.view.View
import android.widget.Spinner
import android.widget.AdapterView
package com.example.photoaivideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GenerateImageActivity : AppCompatActivity() {

    private lateinit var imgReference: ImageView
    private lateinit var btnSelectReference: Button
    private lateinit var chkSaveReference: CheckBox
    private lateinit var txtPrompt: EditText
    private lateinit var txtNegativePrompt: AutoCompleteTextView
    private lateinit var seekSimilarity: SeekBar
    private lateinit var txtSimilarityLabel: TextView
    private lateinit var spinnerRatio: Spinner
    private lateinit var spinnerQuality: Spinner
    private lateinit var spinnerBatchSize: Spinner
    private lateinit var btnGenerateNow: Button
    private lateinit var progressGeneration: ProgressBar

    private var selectedReferenceUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        // bind views
        imgReference = findViewById(R.id.imgReference)
        btnSelectReference = findViewById(R.id.btnSelectReference)
        chkSaveReference = findViewById(R.id.chkSaveReference)
        txtPrompt = findViewById(R.id.txtPrompt)
        txtNegativePrompt = findViewById(R.id.txtNegativePrompt)
        seekSimilarity = findViewById(R.id.seekSimilarity)
        txtSimilarityLabel = findViewById(R.id.txtSimilarityLabel)
    private lateinit var spinnerModelImage: Spinner
        spinnerRatio = findViewById(R.id.spinnerRatio)
        spinnerQuality = findViewById(R.id.spinnerQuality)
        spinnerBatchSize = findViewById(R.id.spinnerBatchSize)
        btnGenerateNow = findViewById(R.id.btnGenerateNow)
        progressGeneration = findViewById(R.id.progressGeneration)

spinnerModelImage.setSelection(0) // ✅ Default: Stable Diffusion
spinnerModelImage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedModel = parent.getItemAtPosition(position).toString()
        // Use selectedModel for image generation
    }
    override fun onNothingSelected(parent: AdapterView<*>) {}
}

spinnerModelImage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedModel = parent.getItemAtPosition(position).toString()
        // ✅ Use selectedModel when generating image
    }
    override fun onNothingSelected(parent: AdapterView<*>) {}
}

        // select reference button
        btnSelectReference.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Reference"), PICK_IMAGE_REQUEST)
        }

        // similarity seekbar
        seekSimilarity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                txtSimilarityLabel.text = "Reference similarity: $progress%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // generate button
        btnGenerateNow.setOnClickListener {
            startGeneration()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedReferenceUri = data?.data
            imgReference.setImageURI(selectedReferenceUri)
            chkSaveReference.visibility = CheckBox.VISIBLE
        }
    }

    private fun startGeneration() {
        val batchSize = spinnerBatchSize.selectedItem.toString().toIntOrNull() ?: 1

        // show progress
        progressGeneration.visibility = ProgressBar.VISIBLE
        btnGenerateNow.isEnabled = false

        // For now, fake-generate by creating placeholder URIs
        val fakeUris = ArrayList<String>()
        repeat(batchSize) {
            fakeUris.add("placeholder_${Random.nextInt(1000)}")
        }

        // hide progress again
        progressGeneration.visibility = ProgressBar.GONE
        btnGenerateNow.isEnabled = true

        // go to results activity
        val intent = Intent(this, GeneratedImageResultsActivity::class.java)
        intent.putStringArrayListExtra("generatedUris", fakeUris)
        startActivity(intent)
    }
}
