package com.iti.pocketshop.features.aichat.presentation

import android.net.Uri
import com.iti.pocketshop.features.aichat.domain.model.AiErrorType
import com.iti.pocketshop.features.aichat.domain.model.ChatMessage

data class AiChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: AiErrorType? = null,
    val isSpeechRecognitionRunning: Boolean = false,
    val isSpeaking: Boolean = false,
    val speakingMessage: String? = null
)
