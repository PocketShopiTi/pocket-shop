package com.iti.pocketshop.features.aichat.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.settings.domain.GetUserSettingsUseCase
import com.iti.pocketshop.common.settings.domain.models.UserSettings
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.pricing.PriceFormatter
import com.iti.pocketshop.features.aichat.data.ChatSessionStore
import com.iti.pocketshop.features.aichat.domain.model.*
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.usecase.AddToCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.GetLocalCartUseCase
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.features.search.domain.usecase.GetSearchResultsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

@HiltViewModel(assistedFactory = AiChatViewModel.Factory::class)
class AiChatViewModel @AssistedInject constructor(
    private val aiRepository: AiRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val getSearchResultsUseCase: GetSearchResultsUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val getLocalCartUseCase: GetLocalCartUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val sessionStore: ChatSessionStore,
    @Assisted private val initialPrompt: String?,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(initialPrompt: String?): AiChatViewModel
    }

    private companion object {
        const val TAG = "AiChatViewModel"
        const val MAX_AGENT_ITERATIONS = 6
    }

    // Restore any in-session conversation so the chat survives navigation / recreation.
    private val _state = MutableStateFlow(AiChatState(messages = sessionStore.messages.value))
    val state = _state.asStateFlow()

    private var currentCart: ShopifyCart? = null
    private var currentUserSettings = UserSettings()
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
        ),
        AiTool(
            name = "search_outfit_item",
            description = "Search for ONE outfit slot (top, bottom, shoes, accessory). " +
                    "Returns the single best-matching real purchasable product filtered by product type and an optional style tag.",
            parameters = listOf(
                AiParameter("query", "string", "Short search keywords, e.g. 'white sneakers'"),
                AiParameter(
                    "category", "string",
                    "Exact store product type, e.g. T-Shirts, Shirts, Hoodies, Jackets, Pants, Jeans, Shorts, Dresses, Shoes, Bags, Accessories"
                ),
                AiParameter(
                    "tag", "string",
                    "Optional single tag like style:casual, occasion:work, season:summer, gender:men, color:black",
                    isRequired = false
                ),
            )
        )
    )

    init {
        viewModelScope.launch {
            getLocalCartUseCase().collect { cart ->
                currentCart = cart
            }
        }
        viewModelScope.launch {
            getUserSettingsUseCase().collect { settings ->
                currentUserSettings = settings
            }
        }
        // Keep the app-wide session in sync with this screen's messages.
        viewModelScope.launch {
            _state.map { it.messages }
                .distinctUntilChanged()
                .collect { sessionStore.setMessages(it) }
        }
        // Seed the outfit prompt only once per distinct prompt: returning from product details
        // recreates this VM with the same initialPrompt, so we skip re-seeding and keep history.
        initialPrompt?.takeIf { it.isNotBlank() && it != sessionStore.lastSeededPrompt }?.let { prompt ->
            sessionStore.markSeeded(prompt)
            val parsed = parseHiddenContext(prompt)
            _state.update {
                it.copy(
                    messages = it.messages + ChatMessage(
                        content = parsed.visibleText,
                        hiddenContext = parsed.hiddenContext,
                        sender = MessageSender.USER,
                    )
                )
            }
            runAgentLoop()
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
            AiChatAction.OnSendMessage -> sendMessage()
            AiChatAction.OnRetry -> retry()
            AiChatAction.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }
            AiChatAction.OnNewChat -> newChat()
            is AiChatAction.OnQuickReplySelected -> sendQuickReply(action.text)
        }
    }

    private fun newChat() {
        sessionStore.clear()
        _state.update {
            it.copy(messages = emptyList(), inputText = "", selectedImageUri = null, error = null)
        }
    }

    private fun sendQuickReply(text: String) {
        val choice = text.trim()
        if (choice.isEmpty()) return
        _state.update {
            it.copy(
                messages = it.messages + ChatMessage(content = choice, sender = MessageSender.USER),
                error = null
            )
        }
        runAgentLoop()
    }

    private fun sendMessage() {
        val rawText = _state.value.inputText.trim()
        val imageUri = _state.value.selectedImageUri
        if (rawText.isEmpty() && imageUri == null) return

        val parsed = parseHiddenContext(rawText)
        val userMessage = ChatMessage(
            content = parsed.visibleText,
            hiddenContext = parsed.hiddenContext,
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

    private fun retry() {
        // A failed turn drops only the AI placeholder in runAgentLoop's catch block, so the
        // last user (or tool) message is still present. Clear the error and re-run the turn
        // instead of appending a new message (the input field is already empty here).
        if (_state.value.messages.isEmpty()) return
        _state.update { it.copy(error = null) }
        runAgentLoop()
    }

    private fun runAgentLoop() {
        viewModelScope.launch {
            val systemPrompt = buildSystemPrompt()
            var isLooping = true
            var lastToolProducts = emptyList<SearchResultItem.ProductItem>()
            var iterations = 0
            val seenToolCalls = mutableSetOf<String>()

            while (isLooping) {
                iterations++
                if (iterations > MAX_AGENT_ITERATIONS) {
                    Log.w(TAG, "Agent loop exceeded $MAX_AGENT_ITERATIONS iterations, aborting")
                    _state.update { it.copy(messages = it.messages.dropLast(1), error = AiErrorType.GENERIC) }
                    break
                }
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
                val toolCalls = mutableListOf<AiResponse.ToolCall>()

                aiRepository.streamChat(_state.value.messages.dropLast(1), systemPrompt, tools)
                    .catch { e ->
                        Log.e(TAG, "AI chat turn failed", e)
                        _state.update { state ->
                            state.copy(
                                messages = state.messages.dropLast(1),
                                error = AiErrorType.GENERIC
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
                                toolCalls += response
                                updateLastMessage(fullResponseText, isTyping = false, toolCall = response)
                            }
                            is AiResponse.Error -> {
                                _state.update { state ->
                                    state.copy(
                                        messages = state.messages.dropLast(1),
                                        error = response.type
                                    )
                                }
                                isLooping = false
                            }
                            AiResponse.Finished -> {
                                updateLastMessage(fullResponseText, isTyping = false)
                            }
                        }
                    }

                if (_state.value.error != null) {
                    // The turn failed (error already set, placeholder dropped) — stop looping.
                    isLooping = false
                } else if (toolCalls.isNotEmpty()) {
                    val signatures = toolCalls.map { "${it.name}:${it.arguments}" }
                    if (signatures.all { it in seenToolCalls }) {
                        Log.w(TAG, "Model repeated identical tool call(s), aborting loop")
                        _state.update { it.copy(error = AiErrorType.GENERIC) }
                        isLooping = false
                        // still fall through to append tool results is optional; safest to just stop
                    }
                    seenToolCalls += signatures
                    val collectedProducts = mutableListOf<SearchResultItem.ProductItem>()
                    val toolMessages = toolCalls.map { call ->
                        val result = executeTool(call)
                        collectedProducts += result.products
                        ChatMessage(
                            content = result.summary,
                            sender = MessageSender.TOOL,
                            toolCallName = call.name
                        )
                    }
                    lastToolProducts = collectedProducts

                    // Clear the status text if there was no real content
                    if (fullResponseText.isEmpty()) {
                        _state.update { state ->
                            val updatedMessages = state.messages.toMutableList()
                            if (updatedMessages.isNotEmpty()) {
                                val last = updatedMessages.last()
                                updatedMessages[updatedMessages.lastIndex] = last.copy(content = "")
                            }
                            state.copy(messages = updatedMessages + toolMessages)
                        }
                    } else {
                        _state.update { it.copy(messages = it.messages + toolMessages) }
                    }
                } else {
                    // Final assistant turn: pull any [[options]] block out into tappable chips.
                    val parsed = parseQuickReplies(fullResponseText)
                    if (parsed.options.isNotEmpty()) {
                        _state.update { state ->
                            val updatedMessages = state.messages.toMutableList()
                            if (updatedMessages.isNotEmpty()) {
                                val last = updatedMessages.last()
                                updatedMessages[updatedMessages.lastIndex] = last.copy(
                                    content = parsed.visibleText,
                                    quickReplies = parsed.options,
                                )
                            }
                            state.copy(messages = updatedMessages)
                        }
                    }
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
            "search_outfit_item" -> "Finding ${args["category"] ?: "items"}..."
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
                                "- ${p.title} (ID: ${p.id}): ${formatToolPrice(p.price, p.currencyCode)}"
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
                            "Product: ${data.title}\nDescription: ${data.description}\nVariants: ${data.variants.joinToString { v -> "${v.id} - ${formatToolPrice(v.price.amount, v.price.currencyCode)}" }}"
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
            "search_outfit_item" -> {
                val category = toolCall.arguments["category"] as? String ?: ""
                val tag = (toolCall.arguments["tag"] as? String)?.takeIf { it.isNotBlank() }
                val query = (toolCall.arguments["query"] as? String).orEmpty().ifBlank { category }
                val filters = buildList {
                    if (category.isNotBlank()) add(JSONObject().put("productType", category).toString())
                    tag?.let { add(JSONObject().put("tag", it).toString()) }
                }
                when (val result = getSearchResultsUseCase(query, first = 1, filters = filters)) {
                    is PocketResult.Success -> {
                        val products = result.data.products.take(1)
                        ToolExecutionResult(
                            summary = if (products.isEmpty()) {
                                "No products found for category '$category'${tag?.let { " with tag $it" } ?: ""}. Try again without the tag."
                            } else {
                                products.joinToString("\n") { p: SearchResultItem.ProductItem ->
                                    "- ${p.title} (ID: ${p.id}): ${formatToolPrice(p.price, p.currencyCode)}"
                                }
                            },
                            products = products
                        )
                    }
                    is PocketResult.Error -> ToolExecutionResult("Error searching: ${result.error}")
                }
            }
            else -> ToolExecutionResult("Unknown tool")
        }
    }

    private fun formatToolPrice(amount: Double, currencyCode: String): String {
        return PriceFormatter.format(
            amount = amount,
            sourceCurrencyCode = currencyCode,
            userSettings = currentUserSettings,
        )
    }

    private fun buildSystemPrompt(): String {
        return """
            You are a professional AI Shopping Assistant for "Pocket Shop".
            Your goal is to help users find products, manage their cart, and answer shopping questions.
            
            You have access to tools to search for products, get details, and add to cart.
            
            RULES:
            - ALWAYS use the search_products tool if the user asks for something.
            - If the most recent user message includes an image and you haven't already searched for it, call search_products once, then proceed normally.
            - When recommending products, provide their titles and prices.
            - If the user wants to buy or add something, search for it first, then get details for the specific variants, then use add_to_cart with a variantId.
            - Answer politely and professionally.
            - Use Markdown formatting.
            - ONLY answer shopping-related questions.

            OUTFIT BUILDING:
            - When the user asks for an outfit, or to build an outfit around a product, plan 3-4 slots: top, bottom, shoes, accessory.
            - If a product id is given, call get_product_details first and skip the slot that product already fills.
            - Call search_outfit_item exactly once per slot, ONE call at a time. Each call returns ONE product for that slot.
            - Use the tag parameter when style, occasion, season, gender, or color is known (e.g. style:casual, gender:men).
            - If a slot returns no products, retry once without the tag; if still empty, skip the slot and say so.
            - Present exactly one item per category: for each slot state the product title, price, and a one-line reason. NEVER invent products.

            ASKING THE USER TO CHOOSE:
            - When you need the user to pick among a few discrete options (occasion, gender, style, size, color), ask a short question and end that message with [[options]]Option A|Option B|Option C[[/options]] (max 4 options).
            - Do NOT use options for free-form answers. The options block is stripped from the message and shown as tappable buttons.
        """.trimIndent()
    }
}
