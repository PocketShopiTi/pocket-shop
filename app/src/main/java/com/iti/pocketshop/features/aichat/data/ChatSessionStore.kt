package com.iti.pocketshop.features.aichat.data

import com.iti.pocketshop.features.aichat.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-wide in-memory holder for the AI chat conversation. Kept as a [Singleton] so the chat
 * survives navigating away (e.g. tapping a product), screen recreation, and reopening from the
 * FAB. Not persisted across process death — this only guarantees in-session continuity.
 *
 * [lastSeededPrompt] dedupes the auto-sent outfit prompt: the same seed (same product) is only
 * sent once, so returning from product details restores the conversation instead of re-asking.
 */
@Singleton
class ChatSessionStore @Inject constructor() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    var lastSeededPrompt: String? = null
        private set

    fun setMessages(list: List<ChatMessage>) {
        _messages.value = list
    }

    fun markSeeded(prompt: String) {
        lastSeededPrompt = prompt
    }

    fun clear() {
        _messages.value = emptyList()
        lastSeededPrompt = null
    }
}
