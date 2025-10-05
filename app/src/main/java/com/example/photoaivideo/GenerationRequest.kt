package com.example.photoaivideo

import java.io.Serializable

/**
 * Represents a complete image generation request, compatible with
 * AUTOMATIC1111-style parameters.
 */
data class GenerationRequest(
    val model: String,                 // Stable Diffusion checkpoint
    val prompts: String,               // Text prompt
    val negativePrompt: String,        // Negative prompt
    val samplingMethod: String,        // Sampler type
    val samplingSteps: Int,            // Steps (10–150)
    val cfgScale: Float,               // CFG Scale (1–20)
    val width: Int,                    // Image width (512–2048)
    val height: Int,                   // Image height (512–2048)
    val batchCount: Int,               // Number of batches (1,2,4)
    val batchSize: Int,                // Images per batch (1,2)
    val seed: String,                  // Seed value
    val hiresFix: Boolean = false,     // High-res fix toggle
    val refiner: Boolean = false,      // Refiner model toggle
    val lora: String = "None",         // Selected LoRA model
    val referenceImageUri: String = "",// For img2img
    val similarity: Float = 1.0f,      // For img2img similarity strength
    val quality: Float = 1.0f          // Optional quality scaling
) : Serializable
