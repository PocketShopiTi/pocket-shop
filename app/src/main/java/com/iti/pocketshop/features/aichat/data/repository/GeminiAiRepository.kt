package com.iti.pocketshop.features.aichat.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.*
import com.iti.pocketshop.features.aichat.domain.model.*
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlinx.serialization.json.*

class GeminiAiRepository @Inject constructor() : AiRepository {

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
            modelName = "gemini-2.5-flash",
            systemInstruction = content { text(systemPrompt) },
            tools = geminiTools
        )

        val history = messages.map { msg ->
            when (msg.sender) {
                MessageSender.USER -> content("user") { text(msg.content) }
                MessageSender.AI -> content("model") { text(msg.content) }
                MessageSender.TOOL -> content("function") {
                    // Placeholder - handled in the last message logic
                }
                else -> content("user") { text(msg.content) }
            }
        }.filter { it.role != "function" }

        val lastMessage = messages.lastOrNull() ?: return@flow
        
        val chat = model.startChat(history.dropLast(1))
        
        val responseFlow = if (lastMessage.sender == MessageSender.TOOL) {
            val responsePart = FunctionResponsePart(
                name = lastMessage.toolCallName ?: "",
                response = JsonObject(mapOf("result" to JsonPrimitive(lastMessage.content)))
            )
            chat.sendMessageStream(content("function") { part(responsePart) })
        } else {
            chat.sendMessageStream(lastMessage.content)
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
}
