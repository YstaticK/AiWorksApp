package com.example.photoaivideo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Model(
    val provider: String,
    val name: String
)

object ProviderRegistry {
    private const val FILE_NAME = "providers.json"

    // Default providers + models
    private val defaults = listOf(
        Model("OpenAI", "DALL·E 2"),
        Model("OpenAI", "DALL·E 3"),
        Model("Stability AI", "Stable Diffusion v1.5"),
        Model("Stability AI", "Stable Diffusion XL")
    )

    fun loadAll(context: Context): List<Model> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            saveAll(context, defaults)
            return defaults
        }
        return try {
            val arr = JSONArray(file.readText())
            val list = mutableListOf<Model>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(Model(obj.getString("provider"), obj.getString("name")))
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            defaults
        }
    }

    fun saveAll(context: Context, models: List<Model>) {
        val arr = JSONArray()
        for (m in models) {
            val obj = JSONObject()
            obj.put("provider", m.provider)
            obj.put("name", m.name)
            arr.put(obj)
        }
        File(context.filesDir, FILE_NAME).writeText(arr.toString())
    }
}
