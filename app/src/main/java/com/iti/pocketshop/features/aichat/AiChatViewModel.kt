package com.iti.pocketshop.features.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.aichat.domain.model.AiParameter
import com.iti.pocketshop.features.aichat.domain.model.AiResponse
import com.iti.pocketshop.features.aichat.domain.model.AiTool
import com.iti.pocketshop.features.aichat.domain.model.ChatMessage
import com.iti.pocketshop.features.aichat.domain.model.MessageSender
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.usecase.AddToCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.GetLocalCartUseCase
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import com.iti.pocketshop.features.search.domain.usecase.GetSearchResultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val getSearchResultsUseCase: GetSearchResultsUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val getLocalCartUseCase: GetLocalCartUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatState())
    val state = _state.asStateFlow()

    private var currentCart: ShopifyCart? = null
    private val tools = listOf(
        AiTool(
            name = "search_products",
            description = "Search for products in the store by query string.",
            parameters = listOf(AiParameter("query", "string", "The search term"))
        ),
        AiTool(
            name = "get_product_details",
            description = "Get detailed information about a specific product including variants and prices.",
            parameters = listOf(AiParameter("productId", "string", "The ID of the product"))
        ),
        AiTool(
            name = "add_to_cart",
            description = "Add a product variant to the user's shopping cart.",
            parameters = listOf(
                AiParameter("variantId", "string", "The ID of the product variant"),
                AiParameter("quantity", "integer", "Number of items to add")
            )
        ),
        AiTool(
            name = "get_cart",
            description = "Get the current contents of the user's shopping cart.",
            parameters = emptyList()
        )
    )

    init {
        viewModelScope.launch {
            getLocalCartUseCase().collect { cart ->
                currentCart = cart
            }
        }
    }

    fun onAction(action: AiChatAction) {
        when (action) {
            is AiChatAction.OnTextChanged -> {
                _state.update { it.copy(inputText = action.text) }
            }
            AiChatAction.OnSendMessage -> sendMessage()
            AiChatAction.OnRetry -> sendMessage()
        }
    }

    private fun sendMessage() {
        val userText = _state.value.inputText.trim()
        if (userText.isEmpty()) return

        val userMessage = ChatMessage(content = userText, sender = MessageSender.USER)
        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                error = null
            )
        }

        runAgentLoop()
    }

    private fun runAgentLoop() {
        viewModelScope.launch {
            val systemPrompt = buildSystemPrompt()
            var isLooping = true
            
            while (isLooping) {
                val aiPlaceholder = ChatMessage(content = "", sender = MessageSender.AI, isTyping = true)
                _state.update { it.copy(messages = it.messages + aiPlaceholder) }

                var fullResponseText = ""
                var toolCall: AiResponse.ToolCall? = null

                aiRepository.streamChat(_state.value.messages.dropLast(1), systemPrompt, tools)
                    .catch { e ->
                        _state.update { state ->
                            state.copy(
                                messages = state.messages.dropLast(1),
                                error = e.message ?: "Something went wrong"
                            )
                        }
                        isLooping = false
                    }
                    .collect { response ->
                        when (response) {
                            is AiResponse.TextChunk -> {
                                fullResponseText += response.text
                                updateLastMessage(fullResponseText, isTyping = true)
                            }
                            is AiResponse.ToolCall -> {
                                toolCall = response
                            }
                            AiResponse.Finished -> {
                                updateLastMessage(fullResponseText, isTyping = false)
                            }
                        }
                    }

                if (toolCall != null) {
                    val result = executeTool(toolCall)
                    val toolMessage = ChatMessage(
                        content = result,
                        sender = MessageSender.TOOL,
                        toolCallName = toolCall.name
                    )
                    _state.update { it.copy(messages = it.messages + toolMessage) }
                    // Continue loop to let AI process tool result
                } else {
                    isLooping = false
                }
            }
        }
    }

    private fun updateLastMessage(content: String, isTyping: Boolean) {
        _state.update { state ->
            val updatedMessages = state.messages.toMutableList()
            if (updatedMessages.isNotEmpty()) {
                updatedMessages[updatedMessages.lastIndex] = updatedMessages.last().copy(
                    content = content,
                    isTyping = isTyping
                )
            }
            state.copy(messages = updatedMessages)
        }
    }

    private suspend fun executeTool(toolCall: AiResponse.ToolCall): String {
        return when (toolCall.name) {
            "search_products" -> {
                val query = toolCall.arguments["query"] as? String ?: ""
                when (val result = getSearchResultsUseCase(query)) {
                    is PocketResult.Success -> result.data.products.joinToString("\n") { p -> 
                        "- ${p.title} (ID: ${p.id}): ${p.price} ${p.currencyCode}" 
                    }
                    is PocketResult.Error -> "Error searching products: ${result.error}"
                }
            }
            "get_product_details" -> {
                val productId = toolCall.arguments["productId"] as? String ?: ""
                val result = getProductDetailsUseCase(productId)
                if (result.isSuccess) {
                    val data = result.getOrThrow()
                    "Product: ${data.title}\nDescription: ${data.description}\nVariants: ${data.variants.joinToString { v -> "${v.id} - ${v.price.amount} ${v.price.currencyCode}" }}"
                } else {
                    "Error getting details: ${result.exceptionOrNull()}"
                }
            }
            "add_to_cart" -> {
                val cartId = currentCart?.id ?: ""
                val variantId = toolCall.arguments["variantId"] as? String ?: ""
                val quantity = toolCall.arguments["quantity"]?.toString()?.toIntOrNull() ?: 1
                if (cartId.isEmpty()) return "Error: No active cart found."
                when (val result = addToCartUseCase(cartId, variantId, quantity)) {
                    is PocketResult.Success -> "Successfully added to cart!"
                    is PocketResult.Error -> "Failed to add to cart: ${result.error}"
                }
            }
            "get_cart" -> {
                currentCart?.lines?.joinToString("\n") { "- ${it.title} (${it.quantity}x)" } ?: "Cart is empty"
            }
            else -> "Unknown tool"
        }
    }

    private fun buildSystemPrompt(): String {
        return """
            You are a professional AI Shopping Assistant for "Pocket Shop".
            Your goal is to help users find products, manage their cart, and answer shopping questions.
            
            You have access to tools to search for products, get details, and add to cart.
            
            RULES:
            - ALWAYS use the search_products tool if the user asks for something you don't have information about.
            - When recommending products, provide their titles and prices.
            - If the user wants to buy or add something, search for it first, then get details for the specific variants, then use add_to_cart with a variantId.
            - Answer politely and professionally.
            - Use Markdown formatting.
            - ONLY answer shopping-related questions.
        """.trimIndent()
    }
}
