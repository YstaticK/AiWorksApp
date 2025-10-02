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

    // Hardcoded single default: Local Stable Diffusion
    private val knownDefaults = listOf(
        Provider(
            "LocalSD",
            null,
            mutableListOf("stable-diffusion"),
            "http://192.168.178.27:7860"
        )
    )

    fun getKnownDefaults(): List<Provider> {
        return knownDefaults.map { it.copy() }
    }

    fun getDefaultProviderByName(name: String): Provider? {
        return knownDefaults.find { it.name.equals(name, ignoreCase = true) }
    }

    // Allowed sizes for LocalSD
    val modelSizeConstraints: Map<String, List<String>> = mapOf(
        "stable-diffusion" to listOf("512x512", "768x768", "1024x1024")
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

    fun resetProviders(context: Context): MutableList<Provider> {
        saveAll(context, knownDefaults)
        return knownDefaults.toMutableList()
    }
}
