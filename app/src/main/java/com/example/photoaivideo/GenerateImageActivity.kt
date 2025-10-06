package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {

    private lateinit var btnTxt2Img: Button
    private lateinit var btnImg2Img: Button
    private lateinit var container: FrameLayout
    private val TAG = "GenerateImageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        btnTxt2Img = findViewById(R.id.btnTxt2Img)
        btnImg2Img = findViewById(R.id.btnImg2Img)
        container = findViewById(R.id.modeContainer)

        loadTxt2ImgLayout()
        setActiveTab(true)

        btnTxt2Img.setOnClickListener {
            setActiveTab(true)
            loadTxt2ImgLayout()
        }

        btnImg2Img.setOnClickListener {
            setActiveTab(false)
            loadImg2ImgLayout()
        }
    }

    private fun setActiveTab(isTxt2Img: Boolean) {
        if (isTxt2Img) {
            btnTxt2Img.setBackgroundColor(getColor(android.R.color.holo_blue_dark))
            btnImg2Img.setBackgroundColor(getColor(android.R.color.darker_gray))
        } else {
            btnImg2Img.setBackgroundColor(getColor(android.R.color.holo_blue_dark))
            btnTxt2Img.setBackgroundColor(getColor(android.R.color.darker_gray))
        }
    }

    /** Inflate and handle txt2img layout */
    private fun loadTxt2ImgLayout() {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.layout_txt2img, container, false)
        container.addView(view)

        val etPrompt = view.findViewById<EditText>(R.id.etPrompt)
        val etNegative = view.findViewById<EditText>(R.id.etNegativePrompt)
        val spinnerSampler = view.findViewById<Spinner>(R.id.spinnerSampler)
        val seekSteps = view.findViewById<SeekBar>(R.id.seekSteps)
        val seekCFG = view.findViewById<SeekBar>(R.id.seekCFG)
        val seekWidth = view.findViewById<SeekBar>(R.id.seekWidth)
        val seekHeight = view.findViewById<SeekBar>(R.id.seekHeight)
        val spinnerBatchCount = view.findViewById<Spinner>(R.id.spinnerBatchCount)
        val spinnerBatchSize = view.findViewById<Spinner>(R.id.spinnerBatchSize)
        val etSeed = view.findViewById<EditText>(R.id.etSeed)
        val btnGenerate = view.findViewById<Button>(R.id.btnGenerateTxt2Img)

        // Populate dropdowns
        spinnerSampler.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Euler a", "DPM++ 2M", "DDIM")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerBatchCount.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("1", "2", "4"))
        spinnerBatchSize.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("1", "2"))

        // Button click
        btnGenerate.setOnClickListener {
            val prompt = etPrompt.text.toString()
            Log.d(TAG, "Generate button pressed. Prompt: $prompt")
            Toast.makeText(this, "Starting generation: ${prompt.take(50)}", Toast.LENGTH_SHORT).show()

            val request = GenerationRequest(
                provider = "LocalSD",
                model = "stable-diffusion",
                prompts = prompt,
                negativePrompt = etNegative.text.toString(),
                samplingMethod = spinnerSampler.selectedItem.toString(),
                samplingSteps = seekSteps.progress + 10,
                cfgScale = (seekCFG.progress + 1).toFloat(),
                width = seekWidth.progress + 512,
                height = seekHeight.progress + 512,
                batchCount = spinnerBatchCount.selectedItem.toString().toInt(),
                batchSize = spinnerBatchSize.selectedItem.toString().toInt(),
                seed = etSeed.text.toString()
            )

            val intent = Intent(this, GeneratedImageResultsActivity::class.java)
            intent.putExtra("generationRequest", request)
            startActivity(intent)
        }
    }

    private fun loadImg2ImgLayout() {
        container.removeAllViews()
        val textView = TextView(this)
        textView.text = "img2img layout coming soon..."
        textView.textSize = 18f
        textView.setPadding(24, 24, 24, 24)
        container.addView(textView)
    }
}
