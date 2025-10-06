package com.example.photoaivideo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object StableDiffusionClient {

    // Change this if your local IP changes
    private const val BASE_URL = "http://192.168.178.27:7860"

    /** Send txt2img request to AUTOMATIC1111 */
    fun txt2img(request: GenerationRequest): Bitmap? {
        val url = URL("$BASE_URL/sdapi/v1/txt2img")
        val json = JSONObject().apply {
            put("prompt", request.prompts)
            put("negative_prompt", request.negativePrompt)
            put("sampler_name", request.samplingMethod)
            put("steps", request.samplingSteps)
            put("cfg_scale", request.cfgScale)
            put("width", request.width)
            put("height", request.height)
            put("batch_count", request.batchCount)
            put("batch_size", request.batchSize)
            put("seed", request.seed.ifEmpty { -1 })
            if (request.hiresFix) put("enable_hr", true)
        }

        return postAndDecodeImage(url, json)
    }

    /** Send img2img request with base64-encoded image */
    fun img2img(request: GenerationRequest, imageBase64: String): Bitmap? {
        val url = URL("$BASE_URL/sdapi/v1/img2img")
        val json = JSONObject().apply {
            put("init_images", listOf(imageBase64))
            put("prompt", request.prompts)
            put("negative_prompt", request.negativePrompt)
            put("sampler_name", request.samplingMethod)
            put("steps", request.samplingSteps)
            put("cfg_scale", request.cfgScale)
            put("width", request.width)
            put("height", request.height)
            put("batch_count", request.batchCount)
            put("batch_size", request.batchSize)
            put("seed", request.seed.ifEmpty { -1 })
            if (request.hiresFix) put("enable_hr", true)
        }

        return postAndDecodeImage(url, json)
    }

    /** Common POST logic for both txt2img and img2img */
    private fun postAndDecodeImage(url: URL, body: JSONObject): Bitmap? {
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            connectTimeout = 30000
            readTimeout = 30000
        }

        conn.outputStream.use { it.write(body.toString().toByteArray()) }

        val responseCode = conn.responseCode
        if (responseCode != 200) {
            val errorMsg = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
            println("StableDiffusionClient Error $responseCode: $errorMsg")
            conn.disconnect()
            return null
        }

        val response = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()

        val images = JSONObject(response).optJSONArray("images") ?: return null
        if (images.length() == 0) return null

        val imageBase64 = images.getString(0)
        val decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
