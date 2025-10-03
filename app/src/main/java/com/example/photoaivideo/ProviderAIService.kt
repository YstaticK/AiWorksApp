package com.example.photoaivideo

import android.content.Context
import android.net.Uri
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
        val apiKey = ProviderRegistry.getApiKey(context, provider) // not used for LocalSD, kept for future

        if (baseUrl.isNullOrEmpty()) {
            callback(null, "Missing Base URL for $provider")
            return null
        }

        return when (provider) {
            "LocalSD" -> callLocalSD(baseUrl, model, prompt, width, height, n, referenceImageUri, callback)
            else -> {
                callback(null, "Provider $provider not supported in this build.")
                null
            }
        }
    }

    /** Try to read a path or content:// URI into base64 */
    private fun toBase64(ref: String): String? {
        return try {
            if (ref.startsWith("content://")) {
                context.contentResolver.openInputStream(Uri.parse(ref))?.use { input ->
                    val bytes = input.readBytes()
                    android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                }
            } else {
                // Assume it's a filesystem path
                val file = File(ref)
                if (file.exists()) {
                    val bytes = file.readBytes()
                    android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                } else null
            }
        } catch (e: Exception) {
            null
        }
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
        val isImg2Img = referenceImageUri != null
        val url = if (isImg2Img) {
            "$baseUrl/sdapi/v1/img2img"
        } else {
            "$baseUrl/sdapi/v1/txt2img"
        }

        val bodyJson = JSONObject().apply {
            put("prompt", prompt)
            put("width", width)
            put("height", height)
            put("batch_size", n)
            put("steps", 20)
            put("sampler_index", "Euler a")
            // (Optional) set the model via override if your server uses it:
            // put("override_settings", JSONObject().apply { put("sd_model_checkpoint", model) })

            if (isImg2Img) {
                val base64 = toBase64(referenceImageUri!!)
                if (base64 == null) {
                    // Fail fast with a helpful message
                    put("init_images", emptyList<String>())
                } else {
                    put("init_images", listOf(base64))
                }
                put("denoising_strength", 0.7)
            }
        }

        val requestBody = bodyJson
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
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
