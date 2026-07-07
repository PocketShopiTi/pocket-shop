package com.iti.pocketshop.features.aichat.domain.model

enum class MessageSender {
    USER, AI, SYSTEM, TOOL
}

data class ChatMessage(
    val content: String,
    val sender: MessageSender,
    val isTyping: Boolean = false,
    val isError: Boolean = false,
    val toolCallName: String? = null,
    val toolCallId: String? = null
)
