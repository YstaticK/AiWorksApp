package com.example.photoaivideo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Provider(
    var name: String,
    var apiKey: String? = null,
    var models: MutableList<String> = mutableListOf(),
    var baseUrl: String? = null
)

object ProviderRegistry {
    private const val FILE_NAME = "providers.json"

    // Hardcoded known defaults
    private val knownDefaults = listOf(
        Provider(
            "OpenAI",
            null,
            mutableListOf("DALL·E 2", "DALL·E 3"),
            "https://api.openai.com/v1"
        ),
        Provider(
            "Stability AI",
            null,
            mutableListOf("Stable Diffusion v1.5", "Stable Diffusion XL"),
            "https://api.stability.ai"
        ),
        Provider(
            "MidJourney",
            null,
            mutableListOf(),
            "https://api.midjourney.com" // placeholder
        ),
        Provider(
            "Leonardo.AI",
            null,
            mutableListOf(),
            "https://cloud.leonardo.ai/api" // placeholder
        ),
        Provider(
            "RunDiffusion",
            null,
            mutableListOf(),
            "https://api.rundiffusion.com" // placeholder
        )
    )

    fun loadAll(context: Context): MutableList<Provider> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            saveAll(context, knownDefaults)
            return knownDefaults.toMutableList()
        }

        return try {
            val arr = JSONArray(file.readText())
            val list = mutableListOf<Provider>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val name = obj.getString("name")
                val apiKey = obj.optString("apiKey").ifEmpty { null }
                val baseUrl = obj.optString("baseUrl").ifEmpty { null }
                val modelsArr = obj.optJSONArray("models") ?: JSONArray()
                val models = mutableListOf<String>()
                for (j in 0 until modelsArr.length()) {
                    models.add(modelsArr.getString(j))
                }

                // If provider matches a known default, autofill baseUrl + models if missing
                val default = knownDefaults.find { it.name == name }
                val finalBaseUrl = baseUrl ?: default?.baseUrl
                val finalModels = if (models.isEmpty() && default != null) {
                    default.models.toMutableList()
                } else {
                    models
                }

                list.add(Provider(name, apiKey, finalModels, finalBaseUrl))
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            knownDefaults.toMutableList()
        }
    }

    fun saveAll(context: Context, providers: List<Provider>) {
        val arr = JSONArray()
        for (p in providers) {
            val obj = JSONObject()
            obj.put("name", p.name)
            obj.put("apiKey", p.apiKey ?: "")
            obj.put("baseUrl", p.baseUrl ?: "")
            val modelsArr = JSONArray()
            p.models.forEach { modelsArr.put(it) }
            obj.put("models", modelsArr)
            arr.put(obj)
        }
        File(context.filesDir, FILE_NAME).writeText(arr.toString())
    }

    fun getApiKey(context: Context, providerName: String): String? {
        val providers = loadAll(context)
        return providers.find { it.name == providerName }?.apiKey
    }

    fun getBaseUrl(context: Context, providerName: String): String? {
        val providers = loadAll(context)
        return providers.find { it.name == providerName }?.baseUrl
    }
}
