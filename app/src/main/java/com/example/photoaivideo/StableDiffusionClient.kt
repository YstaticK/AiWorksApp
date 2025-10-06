package com.example.photoaivideo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object StableDiffusionClient {

    private const val BASE_URL = "http://192.168.178.27:7860"

    class SDResponse(
        val bitmap: Bitmap? = null,
        val error: String? = null
    )

    fun txt2img(request: GenerationRequest): SDResponse {
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
        return post(url, json)
    }

    fun img2img(request: GenerationRequest, imageBase64: String): SDResponse {
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
        return post(url, json)
    }

    private fun post(url: URL, body: JSONObject): SDResponse {
        return try {
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 20000
                readTimeout = 60000
            }

            conn.outputStream.use { it.write(body.toString().toByteArray()) }

            val code = conn.responseCode
            val text = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() } ?: ""

            if (code != 200) {
                return SDResponse(error = "HTTP $code\n$text")
            }

            val json = JSONObject(text)
            val images = json.optJSONArray("images") ?: return SDResponse(error = "No images field in response")
            if (images.length() == 0) return SDResponse(error = "Empty image array")

            val imageBase64 = images.getString(0)
            val bytes = Base64.decode(imageBase64, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            SDResponse(bitmap = bmp)
        } catch (e: Exception) {
            SDResponse(error = e.stackTraceToString())
        }
    }
}
