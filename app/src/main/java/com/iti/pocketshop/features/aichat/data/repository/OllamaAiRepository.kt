package com.iti.pocketshop.features.aichat.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.features.aichat.domain.model.AiErrorType
import com.iti.pocketshop.features.aichat.domain.model.AiResponse
import com.iti.pocketshop.features.aichat.domain.model.AiTool
import com.iti.pocketshop.features.aichat.domain.model.ChatMessage
import com.iti.pocketshop.features.aichat.domain.model.MessageSender
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject

class OllamaAiRepository @Inject constructor(
    private val httpClient: HttpClient,
    @param:ApplicationContext private val context: Context,
    private val json: Json
) : AiRepository {

    private companion object {
        const val TAG = "OllamaAiRepository"
        const val BASE_URL = "https://ollama.com"
        const val MODEL_NAME = "gemma4"
    }

    override fun streamChat(
        messages: List<ChatMessage>,
        systemPrompt: String,
        tools: List<AiTool>,
        toolResults: Map<String, String>
    ): Flow<AiResponse> = flow {
        val ollamaMessages = messages.map { msg ->
            val role = when (msg.sender) {
                MessageSender.USER -> "user"
                MessageSender.AI -> "assistant"
                MessageSender.SYSTEM -> "system"
                MessageSender.TOOL -> "tool"
            }

            val content = if (msg.sender == MessageSender.USER) {
                buildString {
                    append(msg.content)
                    msg.hiddenContext?.let { ctx ->
                        if (msg.content.isNotBlank()) append("\n\n")
                        append(ctx)
                    }
                }
            } else msg.content

            val images = msg.imageUri?.let { uri ->
                uriToBase64(uri)?.let { listOf(it) }
            }

            OllamaMessage(
                role = role,
                content = content,
                images = images
            )
        }.toMutableList()
        
        // Prepend system prompt if not empty
        if (systemPrompt.isNotBlank()) {
            ollamaMessages.add(0, OllamaMessage(role = "system", content = systemPrompt))
        }

        val ollamaTools = if (tools.isNotEmpty()) {
            tools.map { tool ->
                OllamaTool(
                    function = OllamaFunction(
                        name = tool.name,
                        description = tool.description,
                        parameters = OllamaParameters(
                            properties = tool.parameters.associate { param ->
                                param.name to OllamaProperty(
                                    type = param.type.lowercase(),
                                    description = param.description
                                )
                            },
                            required = tool.parameters.filter { it.isRequired }.map { it.name }
                        )
                    )
                )
            }
        } else null

        val request = OllamaChatRequest(
            model = MODEL_NAME,
            messages = ollamaMessages,
            stream = true,
            tools = ollamaTools
        )

        try {
            httpClient.preparePost("$BASE_URL/api/chat") {
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.OLLAMA_KEY}")
                contentType(ContentType.Application.Json)
                setBody(request)
                timeout {
                    requestTimeoutMillis = 60_000
                    connectTimeoutMillis = 60_000
                    socketTimeoutMillis = 60_000
                }
            }.execute { response ->
                val channel: ByteReadChannel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (line.isBlank()) continue
                    
                    try {
                        val chatResponse = json.decodeFromString<OllamaChatResponse>(line)
                        
                        // Handle text content
                        chatResponse.message?.content?.let {
                            if (it.isNotEmpty()) emit(AiResponse.TextChunk(it))
                        }
                        
                        // Handle tool calls
                        chatResponse.message?.tool_calls?.forEach { call ->
                            val args = call.function.arguments.mapValues { (_, value) ->
                                when (value) {
                                    is JsonPrimitive -> value.content
                                    else -> value.toString()
                                }
                            }
                            emit(AiResponse.ToolCall(call.function.name, args))
                        }

                        if (chatResponse.done == true) {
                            emit(AiResponse.Finished)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing line: $line", e)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Ollama stream failed", e)
            emit(AiResponse.Error(AiErrorType.GENERIC))
        }
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    @Serializable
    private data class OllamaChatRequest(
        val model: String,
        val messages: List<OllamaMessage>,
        val stream: Boolean = true,
        val tools: List<OllamaTool>? = null
    )

    @Serializable
    private data class OllamaMessage(
        val role: String,
        val content: String,
        val images: List<String>? = null,
        val tool_calls: List<OllamaToolCall>? = null
    )

    @Serializable
    private data class OllamaTool(
        val type: String = "function",
        val function: OllamaFunction
    )

    @Serializable
    private data class OllamaFunction(
        val name: String,
        val description: String,
        val parameters: OllamaParameters
    )

    @Serializable
    private data class OllamaParameters(
        val type: String = "object",
        val properties: Map<String, OllamaProperty>,
        val required: List<String>
    )

    @Serializable
    private data class OllamaProperty(
        val type: String,
        val description: String
    )

    @Serializable
    private data class OllamaToolCall(
        val function: OllamaFunctionCall
    )

    @Serializable
    private data class OllamaFunctionCall(
        val name: String,
        val arguments: Map<String, JsonElement>
    )

    @Serializable
    private data class OllamaChatResponse(
        val model: String? = null,
        val message: OllamaMessage? = null,
        val done: Boolean? = null,
        val error: String? = null
    )
}
