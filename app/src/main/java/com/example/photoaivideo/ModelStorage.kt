package com.example.photoaivideo

import android.content.Context

object ModelStorage {
    private const val PREFS_NAME = "model_prefs"
    private const val KEY_MODELS = "custom_models"

    fun saveModels(context: Context, models: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_MODELS, models.toSet()).apply()
    }

    fun loadModels(context: Context): MutableList<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_MODELS, emptySet())?.toMutableList() ?: mutableListOf()
    }
}
