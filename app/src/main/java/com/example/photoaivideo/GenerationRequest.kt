package com.example.photoaivideo

import java.io.Serializable

data class GenerationRequest(
    val provider: String,
    val model: String,
    val prompts: String,
    val negativePrompt: String?,
    val similarity: Int,
    val seed: String?,
    val width: Int,
    val height: Int,
    val quality: String,
    val batchSize: Int,
    val referenceImageUri: String?
) : Serializable
