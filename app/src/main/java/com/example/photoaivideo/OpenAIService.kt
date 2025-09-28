package com.example.photoaivideo

import android.content.Context
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class OpenAIService(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey = BuildConfig.OPENAI_API_KEY
    private val apiUrl = "https://api.openai.com/v1/images/generations"

    fun generateImage(prompt: String, width: Int, height: Int, callback: (List<File>?) -> Unit) {
        val body = JSONObject()
        body.put("prompt", prompt)
        body.put("n", 2) // generate 2 images for now
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
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(null)
                    return
                }

                val json = JSONObject(response.body()?.string() ?: "{}")
                val dataArray = json.optJSONArray("data")
                val files = mutableListOf<File>()

                if (dataArray != null) {
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
                            files.add(file)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                callback(files)
            }
        })
    }
}
