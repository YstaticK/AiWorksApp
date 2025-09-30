package com.example.photoaivideo

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class Model(
    val provider: String,
    val name: String
)

object ModelStorage {
    private const val FILE_NAME = "models.json"

    private fun getFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    fun getModels(context: Context): MutableList<Model> {
        val file = getFile(context)
        if (!file.exists() || file.readText().isBlank()) {
            // Seed defaults if no file exists
            val defaults = getDefaultModels()
            saveModels(context, defaults)
            return defaults
        }

        val type = object : TypeToken<MutableList<Model>>() {}.type
        return try {
            Gson().fromJson(file.readText(), type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun saveModels(context: Context, models: List<Model>) {
        val file = getFile(context)
        file.writeText(Gson().toJson(models))
    }

    fun addModel(context: Context, model: Model) {
        val models = getModels(context)
        models.add(model)
        saveModels(context, models)
    }

    private fun getDefaultModels(): MutableList<Model> {
        return mutableListOf(
            Model("OpenAI", "DALL·E 2"),
            Model("OpenAI", "DALL·E 3"),
            Model("Stability AI", "Stable Diffusion XL")
        )
    }
}
