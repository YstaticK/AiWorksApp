package com.example.photoaivideo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object StableDiffusionClient {

    private const val TAG = "StableDiffusionClient"
    private const val BASE_URL = "http://192.168.178.27:7860"

    fun txt2img(request: GenerationRequest): Bitmap? =
        post("$BASE_URL/sdapi/v1/txt2img", makeJson(request, null))

    fun img2img(request: GenerationRequest, imageBase64: String): Bitmap? =
        post("$BASE_URL/sdapi/v1/img2img", makeJson(request, imageBase64))

    private fun makeJson(req: GenerationRequest, initImage: String?): JSONObject {
        val j = JSONObject()
        if (initImage != null) j.put("init_images", listOf(initImage))
        j.put("prompt", req.prompts)
        j.put("negative_prompt", req.negativePrompt)
        j.put("sampler_name", req.samplingMethod)
        j.put("steps", req.samplingSteps)
        j.put("cfg_scale", req.cfgScale)
        j.put("width", req.width)
        j.put("height", req.height)
        j.put("batch_count", req.batchCount)
        j.put("batch_size", req.batchSize)
        j.put("seed", req.seed.ifEmpty { -1 })
        if (req.hiresFix) j.put("enable_hr", true)
        return j
    }

    private fun post(endpoint: String, body: JSONObject): Bitmap? {
        return try {
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 30000
                readTimeout = 60000
            }

            conn.outputStream.use { it.write(body.toString().toByteArray()) }

            val code = conn.responseCode
            val text = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() } ?: ""

            Log.d(TAG, "Response ($code): ${text.take(400)}")

            if (code != 200) return null

            val images = JSONObject(text).optJSONArray("images") ?: return null
            if (images.length() == 0) return null
            val img64 = images.getString(0)
            val bytes = Base64.decode(img64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "Request failed", e)
            null
        }
    }
}
