package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GenerateImageActivity : AppCompatActivity() {

    private lateinit var btnTxt2Img: Button
    private lateinit var btnImg2Img: Button
    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_image)

        btnTxt2Img = findViewById(R.id.btnTxt2Img)
        btnImg2Img = findViewById(R.id.btnImg2Img)
        container = findViewById(R.id.modeContainer)

        // Default tab
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
        val spinnerModel = view.findViewById<Spinner>(R.id.spinnerModel)
        val spinnerSampler = view.findViewById<Spinner>(R.id.spinnerSampler)
        val etSeed = view.findViewById<EditText>(R.id.etSeed)
        val btnRandomSeed = view.findViewById<Button>(R.id.btnRandomSeed)
        val seekSteps = view.findViewById<SeekBar>(R.id.seekSteps)
        val txtStepsValue = view.findViewById<TextView>(R.id.txtStepsValue)
        val seekCFG = view.findViewById<SeekBar>(R.id.seekCFG)
        val txtCFGValue = view.findViewById<TextView>(R.id.txtCFGValue)
        val seekWidth = view.findViewById<SeekBar>(R.id.seekWidth)
        val txtWidthValue = view.findViewById<TextView>(R.id.txtWidthValue)
        val seekHeight = view.findViewById<SeekBar>(R.id.seekHeight)
        val txtHeightValue = view.findViewById<TextView>(R.id.txtHeightValue)
        val spinnerBatchCount = view.findViewById<Spinner>(R.id.spinnerBatchCount)
        val spinnerBatchSize = view.findViewById<Spinner>(R.id.spinnerBatchSize)
        val btnGenerate = view.findViewById<Button>(R.id.btnGenerateTxt2Img)

        // Populate model and sampler dropdowns
        spinnerModel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("stable-diffusion")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerSampler.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Euler a", "DPM++ 2M", "DDIM")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerBatchCount.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("1", "2", "4")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerBatchSize.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("1", "2")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Setup seed randomization
        btnRandomSeed.setOnClickListener {
            val randomSeed = (0..999999999).random()
            etSeed.setText(randomSeed.toString())
        }

        // Setup seekbars
        setupSeekBar(seekSteps, txtStepsValue, 10)
        setupSeekBar(seekCFG, txtCFGValue, 7)
        setupSeekBar(seekWidth, txtWidthValue, 512)
        setupSeekBar(seekHeight, txtHeightValue, 512)

        // Generate image button
        btnGenerate.setOnClickListener {
            val steps = seekSteps.progress + 10
            val cfg = seekCFG.progress + 7
            val width = seekWidth.progress + 512
            val height = seekHeight.progress + 512
            val request = GenerationRequest(
                provider = "LocalSD",
                model = spinnerModel.selectedItem.toString(),
                prompts = etPrompt.text.toString(),
                negativePrompt = etNegative.text.toString(),
                steps = steps,
                cfgScale = cfg.toFloat(),
                width = width,
                height = height,
                batchCount = spinnerBatchCount.selectedItem.toString().toInt(),
                batchSize = spinnerBatchSize.selectedItem.toString().toInt(),
                seed = etSeed.text.toString()
            )
            val intent = Intent(this, GeneratedImageResultsActivity::class.java)
            intent.putExtra("generationRequest", request)
            startActivity(intent)
        }
    }

    /** Placeholder for img2img layout */
    private fun loadImg2ImgLayout() {
        container.removeAllViews()
        val textView = TextView(this)
        textView.text = "img2img layout coming soon..."
        textView.textSize = 18f
        textView.setPadding(24, 24, 24, 24)
        container.addView(textView)
    }

    private fun setupSeekBar(seek: SeekBar, txt: TextView, base: Int) {
        txt.text = base.toString()
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                txt.text = (progress + base).toString()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }
}
