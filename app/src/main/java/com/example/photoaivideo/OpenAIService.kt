package com.example.photoaivideo

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class OpenAIService(private val context: Context, private val apiKey: String) {

    private val client = OkHttpClient()
    private val apiUrl = "https://api.openai.com/v1/images/generations"

    fun generateImage(
        prompt: String,
        width: Int,
        height: Int,
        n: Int = 1,
        callback: (List<File>?, String?) -> Unit
    ) {
        val body = JSONObject()
        body.put("prompt", prompt)
        body.put("n", n)
        body.put("size", "${width}x${height}")

        val requestBody = body.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (context as? BasePermissionActivity)?.logErrorToFile("Network request failed", e)
                callback(null, "Network error: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val errorMsg = "API error: ${response.code}"
                    (context as? BasePermissionActivity)?.logErrorToFile(errorMsg, null)
                    callback(null, errorMsg)
                    return
                }

                try {
                    val json = JSONObject(response.body?.string() ?: "{}")
                    val dataArray = json.optJSONArray("data")
                    val files = mutableListOf<File>()

                    if (dataArray != null) {
                        val saveDir = File(context.getExternalFilesDir("images"), "misc")
                        if (!saveDir.exists()) saveDir.mkdirs()

                        for (i in 0 until dataArray.length()) {
                            val imageUrl = dataArray.getJSONObject(i).getString("url")
                            val file = File(saveDir, "ai_${System.currentTimeMillis()}_$i.png")

                            try {
                                val imgReq = Request.Builder().url(imageUrl).build()
                                client.newCall(imgReq).execute().use { resp ->
                                    resp.body?.byteStream()?.use { input ->
                                        file.outputStream().use { output -> input.copyTo(output) }
                                    }
                                }
                                files.add(file)
                            } catch (e: Exception) {
                                (context as? BasePermissionActivity)?.logErrorToFile("Image download failed", e)
                            }
                        }
                    }

                    if (files.isEmpty()) {
                        callback(null, "No images returned by API")
                    } else {
                        callback(files, null)
                    }

                } catch (e: Exception) {
                    (context as? BasePermissionActivity)?.logErrorToFile("Processing API response failed", e)
                    callback(null, "Failed to process response: ${e.localizedMessage}")
                }
            }
        })
    }
}
