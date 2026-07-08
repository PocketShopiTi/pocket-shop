package com.iti.pocketshop.features.aichat.domain.model

import android.net.Uri
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import java.util.UUID

enum class MessageSender {
    USER, AI, SYSTEM, TOOL
}

data class ChatCall(val name: String, val args: Map<String, String>)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val sender: MessageSender,
    val isTyping: Boolean = false,
    val isError: Boolean = false,
    val toolCalls: List<ChatCall> = emptyList(),
    val toolCallName: String? = null, // Still used for TOOL sender to identify which result this is
    val imageUri: Uri? = null,
    val products: List<SearchResultItem.ProductItem> = emptyList(),
    // Extra text sent to the AI with this turn but NOT rendered in the chat bubble
    // (e.g. the product id for outfit generation). See parseHiddenContext.
    val hiddenContext: String? = null,
    // Tappable choices offered by the assistant, rendered as chips. See parseQuickReplies.
    val quickReplies: List<String> = emptyList()
)
