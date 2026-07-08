package com.iti.pocketshop.features.aichat.domain.model

sealed interface AiResponse {
    data class TextChunk(val text: String) : AiResponse
    data class ToolCall(val name: String, val arguments: Map<String, Any?>) : AiResponse
    data object Finished : AiResponse
    data class Error(val type: AiErrorType) : AiResponse
}

enum class AiErrorType {
    QUOTA_EXCEEDED,
    GENERIC
}

data class AiTool(
    val name: String,
    val description: String,
    val parameters: List<AiParameter>
)

data class AiParameter(
    val name: String,
    val type: String, // "string", "number", "integer", "boolean"
    val description: String,
    val isRequired: Boolean = true
)
