package com.iti.pocketshop.features.productdetails.data.datasource

import com.iti.pocketshop.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OllamaAiCompareDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json
) : AiCompareDataSource {

    private companion object {
        const val BASE_URL = "https://ollama.com" // Default for local emulator. Update for cloud.
        const val MODEL_NAME = "gemma4"
    }

    override suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val request = OllamaGenerateRequest(
            model = MODEL_NAME,
            prompt = prompt,
            stream = false
        )

        val responseText = httpClient.post("$BASE_URL/api/generate") {
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.OLLAMA_KEY}")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyAsText()

        val response = json.decodeFromString<OllamaGenerateResponse>(responseText)
        response.response ?: throw IllegalStateException("Ollama returned an empty response")
    }

    @Serializable
    private data class OllamaGenerateRequest(
        val model: String,
        val prompt: String,
        val stream: Boolean = false
    )

    @Serializable
    private data class OllamaGenerateResponse(
        val response: String? = null
    )
}
