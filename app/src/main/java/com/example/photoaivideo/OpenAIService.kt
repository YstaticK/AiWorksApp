package com.example.photoaivideo

import android.content.Context
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class OpenAIService(private val context: Context, private val apiKey: String) {
    private val client = OkHttpClient()
    private val apiUrl = "https://api.openai.com/v1/images/generations"

    // Generate multiple images, callback per image
    fun generateImageStepByStep(
        prompt: String,
        width: Int,
        height: Int,
        batchSize: Int,
        callback: (index: Int, file: File?) -> Unit
    ) {
        val body = JSONObject()
        body.put("prompt", prompt)
        body.put("n", batchSize)
        body.put("size", "${width}x${height}")

        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            body.toString()
        )

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                for (i in 0 until batchSize) callback(i, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    for (i in 0 until batchSize) callback(i, null)
                    return
                }

                val json = JSONObject(response.body()?.string() ?: "{}")
                val dataArray = json.optJSONArray("data") ?: return

                for (i in 0 until dataArray.length()) {
                    val imageUrl = dataArray.getJSONObject(i).getString("url")
                    val saveDir = File(context.getExternalFilesDir("images"), "misc")
                    if (!saveDir.exists()) saveDir.mkdirs()
                    val file = File(saveDir, "ai_${System.currentTimeMillis()}_$i.png")

                    try {
                        val imgReq = Request.Builder().url(imageUrl).build()
                        client.newCall(imgReq).execute().use { resp ->
                            resp.body()?.byteStream()?.use { input ->
                                file.outputStream().use { output -> input.copyTo(output) }
                            }
                        }
                        callback(i, file)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(i, null)
                    }
                }
            }
        })
    }
}
