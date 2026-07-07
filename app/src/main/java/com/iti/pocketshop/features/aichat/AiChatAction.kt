package com.iti.pocketshop.features.aichat

sealed interface AiChatAction {
    data class OnTextChanged(val text: String) : AiChatAction
    data object OnSendMessage : AiChatAction
    data object OnRetry : AiChatAction
}
