package com.example.photoaivideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GenerateVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_video)

val spinnerModelVideo = findViewById<Spinner>(R.id.spinnerModelVideo)
spinnerModelVideo.setSelection(0) // ✅ Default: Runway Gen-2
spinnerModelVideo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedModel = parent.getItemAtPosition(position).toString()
        // Use selectedModel for video generation
    }
    override fun onNothingSelected(parent: AdapterView<*>) {}
}

val spinnerModelVideo = findViewById<Spinner>(R.id.spinnerModelVideo)
spinnerModelVideo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedModel = parent.getItemAtPosition(position).toString()
        // ✅ Use selectedModel when generating video
    }
    override fun onNothingSelected(parent: AdapterView<*>) {}
}

        val generateButton: Button = findViewById(R.id.btnGenerateVideo)
        generateButton.setOnClickListener {
            // Navigate to video results screen (placeholder for now)
            val intent = Intent(this, GeneratedVideoResultsActivity::class.java)
            startActivity(intent)
        }
    }
}
