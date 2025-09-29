package com.example.photoaivideo

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName

data class ImageRequest(
    val model: String,
    val prompt: String,
    @SerializedName("n") val n: Int,
    val size: String
)

data class ImageResponse(
    val data: List<ImageUrl>
)

data class ImageUrl(
    val url: String
)

interface OpenAIApiService {
    @Headers("Content-Type: application/json")
    @POST("images/generations")
    suspend fun generateImage(@Body request: ImageRequest): ImageResponse
}

object OpenAIClient {
    private const val BASE_URL = "https://api.openai.com/v1/"

    fun create(apiKey: String): OpenAIApiService {
        return try {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $apiKey")
                        .build()
                    chain.proceed(request)
                }
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIApiService::class.java)
        } catch (e: Exception) {
            throw RuntimeException("Error creating OpenAIClient: ${e.message}", e)
        }
    }
}
