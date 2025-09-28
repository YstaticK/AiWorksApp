package com.example.photoaivideo

import java.io.Serializable

data class GenerationRequest(
    val model: String,
    val prompts: String,
    val similarity: Int,
    val seed: String?,
    val size: String,
    val quality: String,
    val batchSize: Int,
    val referenceImagePath: String?
) : Serializable
