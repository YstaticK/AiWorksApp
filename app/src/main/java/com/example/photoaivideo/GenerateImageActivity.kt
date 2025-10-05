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

    private fun loadTxt2ImgLayout() {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.layout_txt2img, container, false)
        container.addView(view)

        val etPrompt = view.findViewById<EditText>(R.id.etPrompt)
        val etNegative = view.findViewById<EditText>(R.id.etNegativePrompt)
        val spinnerModel = view.findViewById<Spinner>(R.id.spinnerModel)
        val spinnerSampler = view.findViewById<Spinner>(R.id.spinnerSampler)
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
        val btnGenerate = view.findViewById<Button>(R.id.btnGenerateTxt2Img)

        // populate spinners
        spinnerModel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("stable-diffusion"))
        spinnerSampler.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Euler a", "DPM++ 2M", "DDIM"))

        setupSeekBar(seekSteps, txtStepsValue, 10)
        setupSeekBar(seekWidth, txtWidthValue, 512)
        setupSeekBar(seekHeight, txtHeightValue, 512)
        setupSeekBar(seekCFG, txtCFGValue, 1)
        setupSeekBar(seekBatchCount, txtBatchCountValue, 1)
        setupSeekBar(seekBatchSize, txtBatchSizeValue, 1)

        btnRandomSeed.setOnClickListener {
            etSeed.setText((0..999999999).random().toString())
        }

        btnGenerate.setOnClickListener {
            val request = GenerationRequest(
                provider = "LocalSD",
                model = spinnerModel.selectedItem.toString(),
                prompts = etPrompt.text.toString(),
                negativePrompt = etNegative.text.toString(),
                steps = seekSteps.progress + 10,
                cfg = seekCFG.progress + 1,
                width = seekWidth.progress + 512,
                height = seekHeight.progress + 512,
                batch = seekBatchCount.progress + 1,
                seed = etSeed.text.toString(),
                similarity = 1,
                quality = "normal",
                referenceImageUri = ""
            )
            val intent = Intent(this, GeneratedImageResultsActivity::class.java)
            intent.putExtra("generationRequest", request)
            startActivity(intent)
        }
    }

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
