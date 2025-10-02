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
        callback: (List<File>?, String?) -> Unit
    ) {
        val baseUrl = ProviderRegistry.getBaseUrl(context, provider)
        val apiKey = ProviderRegistry.getApiKey(context, provider)

        if (baseUrl.isNullOrEmpty() || apiKey.isNullOrEmpty()) {
            callback(null, "Missing API key or base URL for $provider")
            return
        }

        when (provider) {
            "OpenAI" -> callOpenAI(apiKey, prompt, width, height, n, callback)
            "Stability AI" -> callStabilityAI(apiKey, prompt, width, height, n, callback)
            else -> callGeneric(baseUrl, apiKey, model, prompt, width, height, n, callback)
        }
    }

    private fun callOpenAI(
        apiKey: String,
        prompt: String,
        width: Int,
        height: Int,
        n: Int,
        callback: (List<File>?, String?) -> Unit
    ) {
        val url = "https://api.openai.com/v1/images/generations"
        val body = JSONObject().apply {
            put("prompt", prompt)
            put("n", n)
            put("size", "${width}x${height}")
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        executeAndSave(request, callback) { json ->
            val dataArray = json.optJSONArray("data")
            (0 until (dataArray?.length() ?: 0)).map { i ->
                dataArray!!.getJSONObject(i).getString("url")
            }
        }
    }

    private fun callStabilityAI(
        apiKey: String,
        prompt: String,
        width: Int,
        height: Int,
        n: Int,
        callback: (List<File>?, String?) -> Unit
    ) {
        val url = "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image"
        val body = JSONObject().apply {
            put("text_prompts", listOf(mapOf("text" to prompt)))
            put("cfg_scale", 7)
            put("height", height)
            put("width", width)
            put("samples", n)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Accept", "application/json")
            .post(body)
            .build()

        executeAndSave(request, callback) { json ->
            val artifacts = json.optJSONArray("artifacts")
            (0 until (artifacts?.length() ?: 0)).map { i ->
                artifacts!!.getJSONObject(i).getString("base64")
            }
        }
    }

    private fun callGeneric(
        baseUrl: String,
        apiKey: String,
        model: String,
        prompt: String,
        width: Int,
        height: Int,
        n: Int,
        callback: (List<File>?, String?) -> Unit
    ) {
        val body = JSONObject().apply {
            put("model", model)
            put("prompt", prompt)
            put("n", n)
            put("size", "${width}x${height}")
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        executeAndSave(request, callback) { json ->
            val dataArray = json.optJSONArray("data")
            (0 until (dataArray?.length() ?: 0)).map { i ->
                dataArray!!.getJSONObject(i).optString("url")
            }
        }
    }

    private fun executeAndSave(
        request: Request,
        callback: (List<File>?, String?) -> Unit,
        extractUrls: (JSONObject) -> List<String>
    ) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(null, "API call failed: ${response.code}")
                    return
                }

                val json = JSONObject(response.body?.string() ?: "{}")
                val urls = extractUrls(json)

                if (urls.isEmpty()) {
                    callback(null, "No images returned")
                    return
                }

                val files = mutableListOf<File>()
                for ((i, url) in urls.withIndex()) {
                    try {
                        val imgReq = Request.Builder().url(url).build()
                        client.newCall(imgReq).execute().use { resp ->
                            val saveDir = File(context.getExternalFilesDir("images"), "misc")
                            if (!saveDir.exists()) saveDir.mkdirs()
                            val file = File(saveDir, "ai_${System.currentTimeMillis()}_$i.png")
                            resp.body?.byteStream()?.use { input ->
                                file.outputStream().use { output -> input.copyTo(output) }
                            }
                            files.add(file)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                callback(files, null)
            }
        })
    }
}
