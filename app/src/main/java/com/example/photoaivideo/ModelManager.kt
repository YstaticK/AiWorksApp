package com.example.photoaivideo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Model(
    val provider: String,
    val name: String
)

object ModelManager {
    private const val FILE_NAME = "models.json"

    fun getModels(context: Context): MutableList<Model> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            // Prepopulate with defaults
            val defaults = mutableListOf(
                Model("OpenAI", "dall-e-3"),
                Model("StabilityAI", "stable-diffusion-xl")
            )
            saveModels(context, defaults)
            return defaults
        }

        val content = file.readText()
        val jsonArray = JSONArray(content)
        val models = mutableListOf<Model>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            models.add(Model(obj.getString("provider"), obj.getString("name")))
        }

        return models
    }

    fun addModel(context: Context, provider: String, name: String) {
        val models = getModels(context)
        models.add(Model(provider, name))
        saveModels(context, models)
    }

    fun removeModel(context: Context, name: String) {
        val models = getModels(context)
        val filtered = models.filterNot { it.name == name }
        saveModels(context, filtered)
    }

    fun saveModels(context: Context, models: List<Model>) {
        val jsonArray = JSONArray()
        for (m in models) {
            val obj = JSONObject()
            obj.put("provider", m.provider)
            obj.put("name", m.name)
            jsonArray.put(obj)
        }
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(jsonArray.toString())
    }
}
