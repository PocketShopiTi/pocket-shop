package com.iti.pocketshop.features.aichat

import com.iti.pocketshop.features.aichat.domain.model.ChatMessage

data class AiChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
