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
                    },
                    optionalParameters = tool.parameters.filterNot { it.isRequired }.map { it.name }
                )
            }))
        } else null

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.5-flash-lite",
            systemInstruction = content { text(systemPrompt) },
            tools = geminiTools
        )

        if (messages.isEmpty()) return@flow

        // Consecutive TOOL messages are merged into ONE "function" content: when the model
        // makes N function calls in a turn, Gemini requires all N responses in a single turn.
        val history = mutableListOf<Content>()
        var pendingToolParts = listOf<FunctionResponsePart>()
        fun flushToolParts() {
            if (pendingToolParts.isNotEmpty()) {
                val parts = pendingToolParts
                history += content("function") { parts.forEach { part(it) } }
                pendingToolParts = emptyList()
            }
        }
        messages.forEach { msg ->
            when (msg.sender) {
                MessageSender.TOOL -> pendingToolParts = pendingToolParts + FunctionResponsePart(
                    name = msg.toolCallName ?: "",
                    response = JsonObject(mapOf("result" to JsonPrimitive(msg.content)))
                )
                MessageSender.USER -> {
                    flushToolParts()
                    history += content("user") {
                        // hiddenContext is sent to the model but never shown in the chat bubble
                        val userText = buildString {
                            append(msg.content)
                            msg.hiddenContext?.let { ctx ->
                                if (msg.content.isNotBlank()) append("\n\n")
                                append(ctx)
                            }
                        }
                        text(userText)
                        msg.imageUri?.let { uri ->
                            val bitmap = uriToBitmap(uri)
                            bitmap?.let { image(it) }
                        }
                    }
                }
                MessageSender.AI -> {
                    flushToolParts()
                    history += content("model") {
                        if (msg.toolCalls.isNotEmpty()) {
                            msg.toolCalls.forEach { call ->
                                part(FunctionCallPart(call.name, call.args.mapValues { JsonPrimitive(it.value) }))
                            }
                        }
                        if (msg.content.isNotEmpty()) {
                            text(msg.content)
                        }
                    }
                }
                else -> {
                    flushToolParts()
                    history += content("user") { text(msg.content) }
                }
            }
        }
        flushToolParts()

        // The last history element is the turn to send (a user message, or the merged
        // function-response block); everything before it seeds the chat.
        val chat = model.startChat(history.dropLast(1))
        val responseFlow = chat.sendMessageStream(history.last())

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
