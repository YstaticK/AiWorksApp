package com.example.photoaivideo

    val model: String,
import java.io.Serializable

data class GenerationRequest(
    val prompts: String,
    val negativePrompt: String?,
    val similarity: Int,
    val seed: String?,
    val width: Int,
    val height: Int,
    val quality: String,
    val batchSize: Int,
    val model: String,
    val referenceImageUri: String?
) : Serializable
