package com.iti.pocketshop.features.aichat

data class AiChatState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)