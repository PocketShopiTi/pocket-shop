package com.iti.pocketshop.features.aichat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.aichat.domain.model.ChatMessage
import com.iti.pocketshop.features.aichat.domain.model.MessageSender
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.features.search.presentation.view.components.SearchProductCard

private val BubbleCornerRadius = 16.dp

@Composable
fun ChatBubble(
    message: ChatMessage,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLastMessage: Boolean = false,
    onQuickReply: (String) -> Unit = {},
) {
    if (message.sender == MessageSender.USER) {
        UserChatBubble(
            message = message,
            modifier = modifier
                .padding(horizontal = 16.dp)
        )
    } else if (message.sender == MessageSender.AI) {
        AssistantChatBubble(
            message = message,
            onProductClick = onProductClick,
            showQuickReplies = isLastMessage,
            onQuickReply = onQuickReply,
            modifier = modifier
        )
    }
}

@Composable
private fun UserChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        MessageContent(
            message = message,
            bubbleColor = MaterialTheme.colorScheme.secondary,
            textColor = MaterialTheme.colorScheme.onSecondary,
            shape = RoundedCornerShape(
                topStart = BubbleCornerRadius,
                topEnd = BubbleCornerRadius,
                bottomEnd = 0.dp,
                bottomStart = BubbleCornerRadius
            ),
            modifier = Modifier
                .padding(start = 40.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssistantChatBubble(
    message: ChatMessage,
    onProductClick: (String) -> Unit,
    showQuickReplies: Boolean,
    onQuickReply: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        if (message.products.isNotEmpty()) {
            ProductRecommendations(
                products = message.products,
                onProductClick = onProductClick
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        MessageContent(
            message = message,
            bubbleColor = MaterialTheme.colorScheme.surfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(
                topStart = BubbleCornerRadius,
                topEnd = BubbleCornerRadius,
                bottomEnd = BubbleCornerRadius,
                bottomStart = 0.dp
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(end = 40.dp)
        )

        if (showQuickReplies && message.quickReplies.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                message.quickReplies.forEach { option ->
                    SuggestionChip(
                        onClick = { onQuickReply(option) },
                        label = { Text(option) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductRecommendations(
    products: List<SearchResultItem.ProductItem>,
    onProductClick: (String) -> Unit,
) {
    Text(
        text = stringResource(R.string.found_products_for_you),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
            .padding(4.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items = products, key = { it.id }) { product ->
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 2.dp,
                modifier = Modifier.width(280.dp)
            ) {
                SearchProductCard(
                    title = product.title,
                    imageUrl = product.imageUrl,
                    imageAlt = product.imageAlt,
                    price = product.price,
                    currencyCode = product.currencyCode,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
private fun MessageContent(
    message: ChatMessage,
    bubbleColor: Color,
    textColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = bubbleColor,
        shape = shape,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        SelectionContainer {
            Column(modifier = Modifier.padding(12.dp)) {
                message.imageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = stringResource(R.string.attached_image),
                        modifier = Modifier
                            .align(Alignment.Start)
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val showTypingOnly = message.isTyping && message.content.isEmpty()
                if (showTypingOnly) {
                    TypingIndicator()
                } else {
                    MarkdownText(
                        text = message.content,
                        color = textColor
                    )
                    if (message.isTyping) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TypingIndicator()
                    }
                }
            }
        }
    }
}