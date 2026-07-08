package com.iti.pocketshop.features.aichat.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.features.aichat.domain.model.MessageSender
import com.iti.pocketshop.features.aichat.presentation.components.ChatBubble
import com.iti.pocketshop.features.aichat.presentation.components.ChatInput
import com.iti.pocketshop.features.aichat.presentation.components.EmptyChatState
import com.iti.pocketshop.features.aichat.presentation.components.ErrorBanner
import kotlinx.coroutines.launch

@Composable
fun AiChatRoot(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    initialPrompt: String? = null,
    viewModel: AiChatViewModel = hiltViewModel(
        creationCallback = { factory: AiChatViewModel.Factory ->
            factory.create(initialPrompt)
        },
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AiChatScreen(
        state = state,
        onBack = onBack,
        onProductClick = onProductClick,
        onAction = viewModel::onAction
    )
}

@Composable
fun AiChatScreen(
    state: AiChatState,
    onProductClick: (String) -> Unit,
    onBack: () -> Unit,
    onAction: (AiChatAction) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let {
            onAction(AiChatAction.OnImageSelected(it))
        } }
    )

    val visibleMessages = remember(state.messages) {
        state.messages.filter {
            (it.sender == MessageSender.USER || it.sender == MessageSender.AI) &&
                    (it.content.isNotBlank() || it.isTyping || it.products.isNotEmpty() || it.imageUri != null)
        }
    }

    val isAtBottom by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisible >= listState.layoutInfo.totalItemsCount - 1
        }
    }

    val lastMessageContentLength = visibleMessages.lastOrNull()?.content?.length ?: 0
    LaunchedEffect(visibleMessages.size, lastMessageContentLength) {
        if (visibleMessages.isNotEmpty() && isAtBottom) {
            listState.animateScrollToItem(visibleMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.shopping_assistant),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        bottomBar = {
            ChatInput(
                text = state.inputText,
                selectedImageUri = state.selectedImageUri,
                onTextChanged = { onAction(AiChatAction.OnTextChanged(it)) },
                onSend = { onAction(AiChatAction.OnSendMessage) },
                onPickImage = {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onRemoveImage = { onAction(AiChatAction.OnImageSelected(null)) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .padding(8.dp)
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedContent (
                    targetState = state.error
                ) { errorMessage ->
                    errorMessage?.let {
                        ErrorBanner(
                            message = stringResource(R.string.ai_chat_error_message),
                            onRetry = { onAction(AiChatAction.OnRetry) },
                            onDismiss = { onAction(AiChatAction.OnDismissError) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                if (visibleMessages.isEmpty() && !state.isLoading) {
                    EmptyChatState(modifier = Modifier.fillMaxWidth().weight(1f))
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = visibleMessages,
                            key = { it.id }
                        ) { message ->
                            ChatBubble(
                                message = message,
                                onProductClick = onProductClick,
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                        item {
                            Spacer(
                                modifier = Modifier
                                    .height(innerPadding.calculateBottomPadding())
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !isAtBottom && visibleMessages.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(visibleMessages.size - 1)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = stringResource(R.string.scroll_to_latest)
                    )
                }
            }
        }
    }
}
