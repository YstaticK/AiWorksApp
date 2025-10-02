package com.example.photoaivideo

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import android.util.Base64

class ProviderAIService(private val context: Context) {

    private val client = OkHttpClient()

    fun generateImage(
        provider: String,
        model: String,
        prompt: String,
        width: Int,
        height: Int,
        n: Int = 1,
        callback: (List<File>?, String?) -> Unit
    ) {
        val baseUrl = ProviderRegistry.getBaseUrl(context, provider)
        if (baseUrl.isNullOrEmpty()) {
            callback(null, "Missing Base URL for $provider")
            return
        }

        callLocalSD(baseUrl, prompt, width, height, n, callback)
    }

    private fun callLocalSD(
        baseUrl: String,
        prompt: String,
        width: Int,
        height: Int,
        n: Int,
        callback: (List<File>?, String?) -> Unit
    ) {
        val url = "$baseUrl/sdapi/v1/txt2img"

        val bodyJson = JSONObject().apply {
            put("prompt", prompt)
            put("width", width)
            put("height", height)
            put("batch_size", n)
        }

        val requestBody = bodyJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    callback(null, "API call failed: ${response.code} - $bodyString")
                    return
                }

                try {
                    val json = JSONObject(bodyString)
                    val images = json.optJSONArray("images")
                    if (images == null || images.length() == 0) {
                        callback(null, "No images returned: $bodyString")
                        return
                    }

                    val files = mutableListOf<File>()
                    for (i in 0 until images.length()) {
                        val base64Image = images.getString(i)
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)

                        val saveDir = File(context.getExternalFilesDir("images"), "misc")
                        if (!saveDir.exists()) saveDir.mkdirs()
                        val file = File(saveDir, "ai_${System.currentTimeMillis()}_$i.png")

                        file.outputStream().use { it.write(imageBytes) }
                        files.add(file)
                    }

                    callback(files, null)
                } catch (e: Exception) {
                    callback(null, "Failed to parse response: ${e.message}\n$bodyString")
                }
            }
        })
    }
}
