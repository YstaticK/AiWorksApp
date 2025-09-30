package com.example.photoaivideo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Model(
    val provider: String,
    val name: String
)

object ModelStorage {
    private const val FILE_NAME = "models.json"

    fun loadModels(context: Context): List<Model> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            // Prepopulate defaults
            val defaults = listOf(
                Model("OpenAI", "DALL·E 2"),
                Model("OpenAI", "DALL·E 3"),
                Model("Stability AI", "Stable Diffusion v1.5"),
                Model("Stability AI", "Stable Diffusion XL")
            )
            saveModels(context, defaults)
            return defaults
        }
        return try {
            val text = file.readText()
            val arr = JSONArray(text)
            val list = mutableListOf<Model>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(Model(obj.getString("provider"), obj.getString("name")))
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveModels(context: Context, models: List<Model>) {
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
