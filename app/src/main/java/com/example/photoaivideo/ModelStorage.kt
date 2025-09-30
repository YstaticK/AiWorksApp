package com.example.photoaivideo

import android.content.Context

// Legacy wrapper around ProviderRegistry for compatibility
object ModelStorage {
    fun loadModels(context: Context): List<Model> {
        return ProviderRegistry.loadAll(context)
    }

    fun saveModels(context: Context, models: List<Model>) {
        ProviderRegistry.saveAll(context, models)
    }
}
