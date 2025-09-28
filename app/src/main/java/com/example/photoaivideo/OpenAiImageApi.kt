package com.example.photoaivideo

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * Simple synchronous helper (not used by the UI flow right now),
 * kept updated to OkHttp 4.x API so it won't break the build.
 */
class OpenAiImageApi(private val apiKey: String) {

    private val client = OkHttpClient()
    private val url = "https://api.openai.com/v1/images/generations"

    fun generateOne(prompt: String, size: String, outputDir: File): File? {
        val jsonBody = JSONObject().apply {
            put("prompt", prompt)
            put("n", 1)
            put("size", size)
        }

        val body: RequestBody =
            jsonBody.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null

            val json = JSONObject(response.body?.string() ?: return null)
            val imageUrl = json.getJSONArray("data").getJSONObject(0).getString("url")

            // Download the image
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
