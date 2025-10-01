package com.example.photoaivideo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val tvInfo: TextView = findViewById(R.id.tvInfo)

        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            "Unknown"
        }

        val infoText = """
            AIWorksApp
            Version: $versionName

            Developed with ❤️ by Your Team.
            (This is a placeholder for credits and details.)
        """.trimIndent()

        tvInfo.text = infoText
    }
}
