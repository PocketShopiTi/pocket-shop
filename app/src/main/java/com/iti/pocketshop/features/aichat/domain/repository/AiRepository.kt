package com.iti.pocketshop.features.aichat.domain.repository

import com.iti.pocketshop.features.aichat.domain.model.AiResponse
import com.iti.pocketshop.features.aichat.domain.model.AiTool
import com.iti.pocketshop.features.aichat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    fun streamChat(
        messages: List<ChatMessage>,
        systemPrompt: String,
        tools: List<AiTool> = emptyList(),
        toolResults: Map<String, String> = emptyMap()
    ): Flow<AiResponse>
}
