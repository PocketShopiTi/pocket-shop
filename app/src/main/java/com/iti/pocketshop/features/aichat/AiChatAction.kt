package com.iti.pocketshop.features.aichat

import android.net.Uri

sealed interface AiChatAction {
    data class OnTextChanged(val text: String) : AiChatAction
    data object OnSendMessage : AiChatAction
    data class OnImageSelected(val uri: Uri?) : AiChatAction
    data object OnRetry : AiChatAction
}
