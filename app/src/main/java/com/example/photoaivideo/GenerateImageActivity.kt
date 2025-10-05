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

    /** Inflate txt2img layout */
    private fun loadTxt2ImgLayout() {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.layout_txt2img, container, false)
        container.addView(view)

        // UI bindings
        val etPrompt = view.findViewById<EditText>(R.id.etPrompt)
        val etNegative = view.findViewById<EditText>(R.id.etNegativePrompt)
        val spinnerModel = view.findViewById<Spinner>(R.id.spinnerModel)
        val spinnerSampler = view.findViewById<Spinner>(R.id.spinnerSampler)
        val chkHiresFix = view.findViewById<CheckBox>(R.id.chkHiresFix)
        val spinnerHiresFix = view.findViewById<Spinner>(R.id.spinnerHiresFix)
        val chkRefiner = view.findViewById<CheckBox>(R.id.chkRefiner)
        val spinnerRefiner = view.findViewById<Spinner>(R.id.spinnerRefiner)
        val seekSteps = view.findViewById<SeekBar>(R.id.seekSteps)
        val txtStepsValue = view.findViewById<TextView>(R.id.txtStepsValue)
        val seekWidth = view.findViewById<SeekBar>(R.id.seekWidth)
        val txtWidthValue = view.findViewById<TextView>(R.id.txtWidthValue)
        val seekHeight = view.findViewById<SeekBar>(R.id.seekHeight)
        val txtHeightValue = view.findViewById<TextView>(R.id.txtHeightValue)
        val seekCFG = view.findViewById<SeekBar>(R.id.seekCFG)
        val txtCFGValue = view.findViewById<TextView>(R.id.txtCFGValue)
        val seekBatchCount = view.findViewById<SeekBar>(R.id.seekBatchCount)
        val txtBatchCountValue = view.findViewById<TextView>(R.id.txtBatchCountValue)
        val seekBatchSize = view.findViewById<SeekBar>(R.id.seekBatchSize)
        val txtBatchSizeValue = view.findViewById<TextView>(R.id.txtBatchSizeValue)
        val etSeed = view.findViewById<EditText>(R.id.etSeed)
        val btnRandomSeed = view.findViewById<Button>(R.id.btnRandomSeed)
        val spinnerLora = view.findViewById<Spinner>(R.id.spinnerLora)
        val btnGenerate = view.findViewById<Button>(R.id.btnGenerateTxt2Img)

        // Populate dropdowns
        spinnerModel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("stable-diffusion")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerSampler.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Euler a", "DPM++ 2M", "DDIM")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerHiresFix.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("None", "Latent", "R-ESRGAN")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerRefiner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("None", "SDXL Refiner")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerLora.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("None", "Style LoRA", "Character LoRA")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // SeekBar value syncing
        setupSeekBar(seekSteps, txtStepsValue, 10)
        setupSeekBar(seekWidth, txtWidthValue, 512)
        setupSeekBar(seekHeight, txtHeightValue, 512)
        setupSeekBar(seekCFG, txtCFGValue, 1)
        setupSeekBar(seekBatchCount, txtBatchCountValue, 1)
        setupSeekBar(seekBatchSize, txtBatchSizeValue, 1)

        // Random seed
        btnRandomSeed.setOnClickListener {
            etSeed.setText((0..999999999).random().toString())
        }

        // Generate button click
        btnGenerate.setOnClickListener {
            val request = GenerationRequest(
                provider = "LocalSD",
                model = spinnerModel.selectedItem.toString(),
                prompt = etPrompt.text.toString(),
                negativePrompt = etNegative.text.toString(),
                samplingMethod = spinnerSampler.selectedItem.toString(),
                samplingSteps = seekSteps.progress + 10,
                cfgScale = (seekCFG.progress + 1).toFloat(),
                width = seekWidth.progress + 512,
                height = seekHeight.progress + 512,
                batchCount = seekBatchCount.progress + 1,
                batchSize = seekBatchSize.progress + 1,
                seed = etSeed.text.toString(),
                hiresFix = chkHiresFix.isChecked,
                refiner = chkRefiner.isChecked,
                lora = spinnerLora.selectedItem.toString(),
                similarity = 1.0f,
                quality = 1.0f,
                referenceImageUri = ""
            )

            val intent = Intent(this, GeneratedImageResultsActivity::class.java)
            intent.putExtra("generationRequest", request)
            startActivity(intent)
        }
    }

    /** Inflate img2img layout */
    private fun loadImg2ImgLayout() {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.layout_img2img, container, false)
        container.addView(view)

        val btnChooseImage = view.findViewById<Button>(R.id.btnChooseImage)
        btnChooseImage.setOnClickListener {
            Toast.makeText(this, "Choose image not yet implemented", Toast.LENGTH_SHORT).show()
        }
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
