package com.iti.pocketshop.features.aichat.presentation

import android.net.Uri

sealed interface AiChatAction {
    data class OnTextChanged(val text: String) : AiChatAction
    data object OnSendMessage : AiChatAction
    data class OnImageSelected(val uri: Uri?) : AiChatAction
    data object OnRetry : AiChatAction
    data object OnDismissError : AiChatAction
    data object OnNewChat : AiChatAction
    data class OnQuickReplySelected(val text: String) : AiChatAction
    data object StartSpeechRecognition : AiChatAction
    data object StopSpeechRecognition : AiChatAction
}
