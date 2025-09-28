package com.example.photoaivideo

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Minimal service for hitting OpenAI's legacy images endpoint that returns URLs,
 * downloading them, and saving into [getExternalFilesDir("images")/misc].
 *
 * NOTE: This expects a valid *user-provided* API key (we read/store it elsewhere).
 */
class OpenAIService(
    private val context: Context,
    private val apiKey: String
) {

    private val client = OkHttpClient()
    private val apiUrl = "https://api.openai.com/v1/images/generations"

    /**
     * Generate images and save to app external storage.
     *
     * @param prompt text prompt
     * @param width image width (e.g., 512)
     * @param height image height (e.g., 512)
     * @param n number of images (default 2)
     * @param callback called with list of saved files or null on failure
     */
    fun generateImage(
        prompt: String,
        width: Int,
        height: Int,
        n: Int = 2,
        callback: (List<File>?) -> Unit
    ) {
        // Build request JSON
        val bodyJson = JSONObject().apply {
            put("prompt", prompt)
            put("n", n)
            put("size", "${width}x${height}")
        }

        val requestBody: RequestBody =
            bodyJson.toString().toRequestBody("application/json".toMediaType())

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

                val bodyStr = response.body?.string() ?: run {
                    callback(null); return
                }

                val files = mutableListOf<File>()
                try {
                    val json = JSONObject(bodyStr)
                    val dataArray = json.optJSONArray("data")
                    if (dataArray != null) {
                        // Ensure /images/misc exists
                        val saveDir = File(context.getExternalFilesDir("images"), "misc")
                        if (!saveDir.exists()) saveDir.mkdirs()

                        for (i in 0 until dataArray.length()) {
                            val imageUrl = dataArray.getJSONObject(i).getString("url")
                            val outFile = File(saveDir, "ai_${System.currentTimeMillis()}_$i.png")

                            // Download each URL synchronously on this background thread
                            val imgReq = Request.Builder().url(imageUrl).build()
                            try {
                                client.newCall(imgReq).execute().use { imgResp ->
                                    if (!imgResp.isSuccessful) return@use
                                    val bytes = imgResp.body?.bytes() ?: return@use
                                    outFile.outputStream().use { it.write(bytes) }
                                    files.add(outFile)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                callback(files)
            }
        })
    }
}
