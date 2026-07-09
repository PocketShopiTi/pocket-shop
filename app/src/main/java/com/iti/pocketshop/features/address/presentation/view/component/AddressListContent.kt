package com.iti.pocketshop.features.address.presentation.view.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AddressListContent(
    state: AddressState,
    onAction: (AddressAction) -> Unit,
) {
    if (state.isLoading && state.addresses.isEmpty()) {
        LoadingPanel()
        return
    }
    val pullToRefreshState = rememberPullToRefreshState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (state.addresses.isEmpty()) {
                EmptyAddressState(
                    customerName = state.customerName,
                    modifier = Modifier.align(Alignment.Center),
                    enabled = !state.isSaving,
                    onAddAddress = { onAction(AddressAction.AddAddressClicked) },
                )
            } else {
                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = state.isLoading,
                    onRefresh = { onAction(AddressAction.Refresh) },
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        LoadingIndicator(
                            state = pullToRefreshState,
                            isRefreshing = state.isLoading,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            top = 10.dp,
                            end = 20.dp,
                            bottom = 20.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {


                        items(
                            items = state.addresses,
                            key = { address -> address.id },
                        ) { address ->
                            SwipeableAddressCard(
                                address = address,
                                enabled = !state.isLoading && !state.isSaving,
                                onEdit = { onAction(AddressAction.EditAddressClicked(address.id)) },
                                onDelete = { onAction(AddressAction.DeleteClicked(address.id)) },
                                onMakeDefault = { onAction(AddressAction.SetDefaultClicked(address.id)) },
                            )
                        }
                    }
                }
            }
        }

        if (state.addresses.isNotEmpty()) {
            BottomAddAddressButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                enabled = !state.isSaving,
                onClick = { onAction(AddressAction.AddAddressClicked) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableAddressCard(
    address: Address,
    enabled: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMakeDefault: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (enabled) {
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> onEdit()
                    SwipeToDismissBoxValue.EndToStart -> onDelete()
                    SwipeToDismissBoxValue.Settled -> Unit
                }
            }
            false // never let the box actually dismiss the item, just snap back
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = enabled,
        enableDismissFromEndToStart = enabled,
        backgroundContent = { SwipeActionBackground(direction = dismissState.dismissDirection) },
    ) {
        AddressCard(
            address = address,
            enabled = enabled,
            onMakeDefault = onMakeDefault,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeActionBackground(direction: SwipeToDismissBoxValue) {
    val (color, icon, alignment, description) = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> SwipeBackgroundSpec(
            color = MaterialTheme.colorScheme.primaryContainer,
            icon = Icons.Filled.Edit,
            alignment = Alignment.CenterStart,
            description = stringResource(R.string.address_content_description_edit),
        )

        SwipeToDismissBoxValue.EndToStart -> SwipeBackgroundSpec(
            color = MaterialTheme.colorScheme.errorContainer,
            icon = Icons.Filled.Delete,
            alignment = Alignment.CenterEnd,
            description = stringResource(R.string.address_content_description_delete),
        )

        SwipeToDismissBoxValue.Settled -> SwipeBackgroundSpec(
            color = MaterialTheme.colorScheme.surface,
            icon = null,
            alignment = Alignment.Center,
            description = null,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment,
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = description,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private data class SwipeBackgroundSpec(
    val color: androidx.compose.ui.graphics.Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector?,
    val alignment: Alignment,
    val description: String?,
)