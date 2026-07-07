package com.iti.pocketshop.features.aichat.domain.model

import android.net.Uri

enum class MessageSender {
    USER, AI, SYSTEM, TOOL
}

data class ChatCall(val name: String, val args: Map<String, String>)

data class ChatMessage(
    val content: String,
    val sender: MessageSender,
    val isTyping: Boolean = false,
    val isError: Boolean = false,
    val toolCalls: List<ChatCall> = emptyList(),
    val toolCallName: String? = null, // Still used for TOOL sender to identify which result this is
    val imageUri: Uri? = null
)
