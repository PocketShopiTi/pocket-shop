package com.iti.pocketshop.features.aichat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.*
import com.iti.pocketshop.features.aichat.presentation.AiChatAction
import com.iti.pocketshop.features.aichat.presentation.AiChatState
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
    state: AiChatState,
    onAction: (AiChatAction) -> Unit,
    modifier: Modifier = Modifier,
    isLastMessage: Boolean = false,
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
            state = state,
            onAction = onAction,
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
    state: AiChatState,
    onAction: (AiChatAction) -> Unit,
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

        if (message.content.isNotBlank() || message.isTyping || message.imageUri != null) {
            MessageContent(
                message = message,
                bubbleColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurface,
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
        }

        if (message.content.isNotBlank() && !message.isTyping) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isSpeakingThis = state.isSpeaking && state.speakingMessage == message.content
                IconButton(
                    onClick = {
                        if (isSpeakingThis) {
                            onAction(AiChatAction.OnStopSpeaking)
                        } else {
                            onAction(AiChatAction.OnSpeakMessage(message.content))
                        }
                    },
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isSpeakingThis) Icons.Default.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isSpeakingThis) "Stop speaking" else "Read aloud",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { onAction(AiChatAction.OnCopyMessage(message.content)) },
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showQuickReplies && message.quickReplies.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                message.quickReplies.forEach { option ->
                    SuggestionChip(
                        onClick = { onAction(AiChatAction.OnQuickReplySelected(option)) },
                        label = { Text(option) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
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
                modifier = Modifier.width(280.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
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