package com.iti.pocketshop.features.orders.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState


import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.core.networkutils.toUserMessage
import com.iti.pocketshop.features.orders.presentation.components.OrderHistoryCard
import com.iti.pocketshop.features.orders.presentation.components.OrdersEmptyContent
import com.iti.pocketshop.features.orders.presentation.components.OrdersLoadingContent
import com.iti.pocketshop.features.orders.presentation.components.OrdersTopBar
import com.iti.pocketshop.features.profile.presentation.components.ProfileErrorCard
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun OrdersRoot(
    onBack: () -> Unit,
    onTrack: (String) -> Unit = {},
    onView: (String) -> Unit = {},
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OrdersScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onTrack = onTrack,
        onView = onView,
    )
}

@Composable
fun OrdersScreen(
    state: OrdersState,
    onAction: (OrdersAction) -> Unit,
    onBack: () -> Unit,
    onTrack: (String) -> Unit,
    onView: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        OrdersTopBar(onBack = onBack)

        when {
            state.isInitialLoading && state.orders.isEmpty() -> OrdersLoadingContent(
                modifier = Modifier.weight(1f),
            )

            state.error != null && state.orders.isEmpty() -> Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                ProfileErrorCard(
                    message = state.error.toUserMessage(context),
                    onReload = { onAction(OrdersAction.Retry) },
                )
            }

            state.orders.isEmpty() -> OrdersEmptyContent(
                modifier = Modifier.weight(1f),
            )

            else -> OrdersList(
                state = state,
                errorMessage = state.error?.toUserMessage(context),
                onLoadNextPage = { onAction(OrdersAction.LoadNextPage) },
                onRetry = { onAction(OrdersAction.Retry) },
                onTrack = onTrack,
                onView = onView,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OrdersList(
    state: OrdersState,
    errorMessage: String?,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit,
    onTrack: (String) -> Unit,
    onView: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, state.orders.size, state.hasNextPage, state.error) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (
                    state.hasNextPage &&
                    state.error == null &&
                    lastVisibleIndex >= state.orders.lastIndex - 2
                ) {
                    onLoadNextPage()
                }
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = state.orders,
            key = { order -> order.id },
        ) { order ->
            OrderHistoryCard(
                order = order,
                onTrack = onTrack,
                onView = onView,
            )
        }

        if (state.isLoadingMore) {
            item(key = "orders_loading_more") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (errorMessage != null) {
            item(key = "orders_pagination_error") {
                ProfileErrorCard(
                    message = errorMessage,
                    onReload = onRetry,
                )
            }
        }
    }
}
