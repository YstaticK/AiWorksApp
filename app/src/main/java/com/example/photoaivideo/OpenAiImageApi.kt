package com.example.photoaivideo

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class OpenAiImageApi(private val client: OkHttpClient, private val apiKey: String) {

    fun generateImage(prompt: String, size: String, outputDir: File): File? {
        val url = "https://api.openai.com/v1/images/generations"

        val jsonBody = JSONObject()
        jsonBody.put("prompt", prompt)
        jsonBody.put("n", 1)
        jsonBody.put("size", size)

        val body = RequestBody.create(
            "application/json".toMediaType(),
            jsonBody.toString()
        )

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val json = JSONObject(response.body?.string() ?: return null)
            val imageUrl = json.getJSONArray("data").getJSONObject(0).getString("url")

            // Download image
            val imgRequest = Request.Builder().url(imageUrl).build()
            client.newCall(imgRequest).execute().use { imgResponse ->
                if (!imgResponse.isSuccessful) return null
                val bytes = imgResponse.body?.bytes() ?: return null
                val outFile = File(outputDir, "generated_${System.currentTimeMillis()}.png")
                FileOutputStream(outFile).use { it.write(bytes) }
                return outFile
            }
        }
    }
}
