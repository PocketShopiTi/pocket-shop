package com.iti.pocketshop.features.aichat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.aichat.domain.model.*
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.usecase.AddToCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.GetLocalCartUseCase
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.features.search.domain.usecase.GetSearchResultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
            is AiChatAction.OnImageSelected -> {
                _state.update { it.copy(selectedImageUri = action.uri) }
            }
            AiChatAction.OnSendMessage,
            AiChatAction.OnRetry -> sendMessage()
            AiChatAction.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun sendMessage() {
        val userText = _state.value.inputText.trim()
        val imageUri = _state.value.selectedImageUri
        if (userText.isEmpty() && imageUri == null) return

        val userMessage = ChatMessage(
            content = userText,
            sender = MessageSender.USER,
            imageUri = imageUri
        )
        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                selectedImageUri = null,
                error = null
            )
        }

        runAgentLoop()
    }

    private fun runAgentLoop() {
        viewModelScope.launch {
            val systemPrompt = buildSystemPrompt()
            var isLooping = true
            var lastToolProducts = emptyList<SearchResultItem.ProductItem>()

            while (isLooping) {
                val currentProducts = lastToolProducts
                lastToolProducts = emptyList()

                val aiPlaceholder = ChatMessage(
                    content = "",
                    sender = MessageSender.AI,
                    isTyping = true,
                    products = currentProducts
                )
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
                                updateLastMessage(fullResponseText, isTyping = false, toolCall = response)
                            }
                            AiResponse.Finished -> {
                                updateLastMessage(fullResponseText, isTyping = false)
                            }
                        }
                    }

                if (toolCall != null) {
                    val result = executeTool(toolCall)
                    lastToolProducts = result.products
                    val toolMessage = ChatMessage(
                        content = result.summary,
                        sender = MessageSender.TOOL,
                        toolCallName = toolCall.name
                    )
                    
                    // Clear the status text if there was no real content
                    if (fullResponseText.isEmpty()) {
                        _state.update { state ->
                            val updatedMessages = state.messages.toMutableList()
                            if (updatedMessages.isNotEmpty()) {
                                val last = updatedMessages.last()
                                updatedMessages[updatedMessages.lastIndex] = last.copy(content = "")
                            }
                            state.copy(messages = updatedMessages + toolMessage)
                        }
                    } else {
                        _state.update { it.copy(messages = it.messages + toolMessage) }
                    }
                } else {
                    isLooping = false
                }
            }
        }
    }

    private fun updateLastMessage(content: String, isTyping: Boolean, toolCall: AiResponse.ToolCall? = null) {
        _state.update { state ->
            val updatedMessages = state.messages.toMutableList()
            if (updatedMessages.isNotEmpty()) {
                val last = updatedMessages.last()
                val newToolCalls = if (toolCall != null) {
                    last.toolCalls + ChatCall(toolCall.name, toolCall.arguments.mapValues { it.value.toString() })
                } else {
                    last.toolCalls
                }

                // If it's empty but has tool calls, show status
                val displayContent = if (content.isEmpty() && (newToolCalls.isNotEmpty() || isTyping)) {
                    if (newToolCalls.isNotEmpty()) {
                        getToolStatusText(newToolCalls.last().name, newToolCalls.last().args)
                    } else {
                        "" // Still typing but no tool call yet
                    }
                } else {
                    content
                }

                updatedMessages[updatedMessages.lastIndex] = last.copy(
                    content = displayContent,
                    isTyping = isTyping,
                    toolCalls = newToolCalls
                )
            }
            state.copy(messages = updatedMessages)
        }
    }

    private fun getToolStatusText(name: String, args: Map<String, String>): String {
        return when (name) {
            "search_products" -> "Searching for '${args["query"] ?: "products"}'..."
            "get_product_details" -> "Getting details..."
            "add_to_cart" -> "Adding to cart..."
            "get_cart" -> "Checking cart..."
            else -> "Processing..."
        }
    }

    private data class ToolExecutionResult(
        val summary: String,
        val products: List<SearchResultItem.ProductItem> = emptyList()
    )

    private suspend fun executeTool(toolCall: AiResponse.ToolCall): ToolExecutionResult {
        return when (toolCall.name) {
            "search_products" -> {
                val query = toolCall.arguments["query"] as? String ?: ""
                when (val result = getSearchResultsUseCase(query)) {
                    is PocketResult.Success -> {
                        val products = result.data.products
                        ToolExecutionResult(
                            summary = products.joinToString("\n") { p: SearchResultItem.ProductItem ->
                                "- ${p.title} (ID: ${p.id}): ${p.price} ${p.currencyCode}"
                            },
                            products = products
                        )
                    }
                    is PocketResult.Error -> ToolExecutionResult("Error searching products: ${result.error}")
                }
            }
            "get_product_details" -> {
                val productId = toolCall.arguments["productId"] as? String ?: ""
                when (val result = getProductDetailsUseCase(productId)) {
                    is PocketResult.Error -> ToolExecutionResult("Error getting details: ${result.error}")
                    is PocketResult.Success -> {
                        val data = result.data
                        ToolExecutionResult(
                            "Product: ${data.title}\nDescription: ${data.description}\nVariants: ${data.variants.joinToString { v -> "${v.id} - ${v.price.amount} ${v.price.currencyCode}" }}"
                        )
                    }
                }
            }
            "add_to_cart" -> {
                val cartId = currentCart?.id ?: ""
                val variantId = toolCall.arguments["variantId"] as? String ?: ""
                val quantity = toolCall.arguments["quantity"]?.toString()?.toIntOrNull() ?: 1
                if (cartId.isEmpty()) ToolExecutionResult("Error: No active cart found.")
                else when (val result = addToCartUseCase(cartId, variantId, quantity)) {
                    is PocketResult.Success -> ToolExecutionResult("Successfully added to cart!")
                    is PocketResult.Error -> ToolExecutionResult("Failed to add to cart: ${result.error}")
                }
            }
            "get_cart" -> {
                ToolExecutionResult(currentCart?.lines?.joinToString("\n") { "- ${it.title} (${it.quantity}x)" } ?: "Cart is empty")
            }
            else -> ToolExecutionResult("Unknown tool")
        }
    }

    private fun buildSystemPrompt(): String {
        return """
            You are a professional AI Shopping Assistant for "Pocket Shop".
            Your goal is to help users find products, manage their cart, and answer shopping questions.
            
            You have access to tools to search for products, get details, and add to cart.
            
            RULES:
            - ALWAYS use the search_products tool if the user asks for something or provides an image.
            - If an image is provided, describe it briefly and search for similar products in the store using search_products.
            - When recommending products, provide their titles and prices.
            - If the user wants to buy or add something, search for it first, then get details for the specific variants, then use add_to_cart with a variantId.
            - Answer politely and professionally.
            - Use Markdown formatting.
            - ONLY answer shopping-related questions.
        """.trimIndent()
    }
}
