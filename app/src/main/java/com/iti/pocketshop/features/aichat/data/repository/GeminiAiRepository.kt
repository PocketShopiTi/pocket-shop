package com.iti.pocketshop.features.aichat.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.*
import com.iti.pocketshop.features.aichat.domain.model.*
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlinx.serialization.json.*

class GeminiAiRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AiRepository {

    override fun streamChat(
        messages: List<ChatMessage>,
        systemPrompt: String,
        tools: List<AiTool>,
        toolResults: Map<String, String>
    ): Flow<AiResponse> = flow {
        val geminiTools = if (tools.isNotEmpty()) {
            listOf(Tool.functionDeclarations(tools.map { tool ->
                FunctionDeclaration(
                    name = tool.name,
                    description = tool.description,
                    parameters = tool.parameters.associate { param ->
                        param.name to when (param.type.lowercase()) {
                            "string" -> Schema.string(param.description)
                            "integer" -> Schema.integer(param.description)
                            "number" -> Schema.double(param.description)
                            "boolean" -> Schema.boolean(param.description)
                            else -> Schema.string(param.description)
                        }
                    }
                )
            }))
        } else null

        val model = Firebase.ai.generativeModel(
            modelName = "gemini-2.5-flash-lite",
            systemInstruction = content { text(systemPrompt) },
            tools = geminiTools
        )

        val history = messages.map { msg ->
            when (msg.sender) {
                MessageSender.USER -> content("user") {
                    text(msg.content)
                    msg.imageUri?.let { uri ->
                        val bitmap = uriToBitmap(uri)
                        bitmap?.let { image(it) }
                    }
                }
                MessageSender.AI -> content("model") {
                    if (msg.toolCalls.isNotEmpty()) {
                        msg.toolCalls.forEach { call ->
                            part(FunctionCallPart(call.name, call.args.mapValues { JsonPrimitive(it.value) }))
                        }
                    }
                    if (msg.content.isNotEmpty()) {
                        text(msg.content)
                    }
                }
                MessageSender.TOOL -> content("function") {
                    part(FunctionResponsePart(
                        name = msg.toolCallName ?: "",
                        response = JsonObject(mapOf("result" to JsonPrimitive(msg.content)))
                    ))
                }
                else -> content("user") { text(msg.content) }
            }
        }

        val lastMessage = messages.lastOrNull() ?: return@flow
        
        val chat = model.startChat(history.dropLast(1))
        
        val responseFlow = if (lastMessage.sender == MessageSender.TOOL) {
            val responsePart = FunctionResponsePart(
                name = lastMessage.toolCallName ?: "",
                response = JsonObject(mapOf("result" to JsonPrimitive(lastMessage.content)))
            )
            chat.sendMessageStream(content("function") { part(responsePart) })
        } else {
            val lastContent = content("user") {
                text(lastMessage.content)
                lastMessage.imageUri?.let { uri ->
                    val bitmap = uriToBitmap(uri)
                    bitmap?.let { image(it) }
                }
            }
            chat.sendMessageStream(lastContent)
        }

        responseFlow.collect { chunk ->
            chunk.text?.let { emit(AiResponse.TextChunk(it)) }
            chunk.functionCalls.forEach { call ->
                val args = call.args.mapValues { (_, value) -> 
                    when (value) {
                        is JsonPrimitive -> value.content
                        else -> value.toString()
                    }
                }
                emit(AiResponse.ToolCall(call.name, args))
            }
        }
        emit(AiResponse.Finished)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
}
