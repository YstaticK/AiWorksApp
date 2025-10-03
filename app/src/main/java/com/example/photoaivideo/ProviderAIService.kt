package com.example.photoaivideo

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ProviderAIService(private val context: Context) {

    private val client = OkHttpClient()

    fun generateImage(
        provider: String,
        model: String,
        prompt: String,
        width: Int,
        height: Int,
        n: Int = 1,
        referenceImageUri: String? = null,
        callback: (List<File>?, String?) -> Unit
    ): Call? {
        val baseUrl = ProviderRegistry.getBaseUrl(context, provider)
        val apiKey = ProviderRegistry.getApiKey(context, provider)

        if (baseUrl.isNullOrEmpty()) {
            callback(null, "Missing Base URL for $provider")
            return null
        }

        // LocalSD (AUTOMATIC1111 style API)
        if (provider == "LocalSD") {
            return callLocalSD(baseUrl, model, prompt, width, height, n, referenceImageUri, callback)
        }

        // Fallback: unsupported provider
        callback(null, "Provider $provider not supported in this build.")
        return null
    }

    private fun callLocalSD(
        baseUrl: String,
        model: String,
        prompt: String,
        width: Int,
        height: Int,
        n: Int,
        referenceImageUri: String?,
        callback: (List<File>?, String?) -> Unit
    ): Call {
        val url = if (referenceImageUri != null) {
            "$baseUrl/sdapi/v1/img2img"
        } else {
            "$baseUrl/sdapi/v1/txt2img"
        }

        val body = JSONObject().apply {
            put("prompt", prompt)
            put("width", width)
            put("height", height)
            put("batch_size", n)
            put("steps", 20)
            put("sampler_index", "Euler a")
            if (referenceImageUri != null) {
                put("init_images", listOf(referenceImageUri))
                put("denoising_strength", 0.7)
            }
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
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
                        val base64Data = images.getString(i)
                        val imageBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                        val saveDir = File(context.getExternalFilesDir("images"), "misc")
                        if (!saveDir.exists()) saveDir.mkdirs()
                        val file = File(saveDir, "ai_${System.currentTimeMillis()}_$i.png")
                        file.outputStream().use { it.write(imageBytes) }
                        files.add(file)
                    }
                    callback(files, null)
                } catch (e: Exception) {
                    callback(null, "Parsing error: ${e.message}")
                }
            }
        })

        return call
    }
}
