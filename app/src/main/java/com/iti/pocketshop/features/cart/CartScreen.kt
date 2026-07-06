package com.iti.pocketshop.features.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.features.cart.components.CartEmptyState
import com.iti.pocketshop.features.cart.components.CartItemCard
import com.iti.pocketshop.features.cart.components.OrderSummaryCard
import com.iti.pocketshop.features.cart.components.RemoveCartItemDialog
import java.util.Locale

@Composable
fun CartRoot(
    onStartShoppingClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CartScreen(
        state = state,
        onStartShoppingClick = onStartShoppingClick,
        onCheckoutClick = onCheckoutClick
    ) { viewModel.onAction(it) }
}

@Composable
fun CartScreen(
    state: CartState,
    onStartShoppingClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onAction: (CartAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.cart),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            actions = {
                if (state.itemsCounts > 0) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.items_count, state.itemsCounts),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            windowInsets = TopAppBarDefaults.windowInsets.exclude(WindowInsets.statusBars),
        )

        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(CartAction.FetchCart) },
            modifier = Modifier
                .weight(1f)
        ) {
            if (state.items.isEmpty()) {
                CartEmptyState(
                    onStartShoppingClick = onStartShoppingClick,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.items, key = { it.lineId }) { item ->
                        CartItemCard(
                            item = item,
                            onRemoveClick = { onAction(CartAction.PrepareDeletingItem(item)) },
                            onUpdateQuantity = { qty ->
                                onAction(
                                    CartAction.UpdateQuantity(
                                        item.lineId,
                                        qty
                                    )
                                )
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        OrderSummaryCard(
                            subTotal = state.subTotal,
                            shipping = state.shipping,
                            total = state.total
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        if (state.items.isNotEmpty()) {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .padding(24.dp)
                ) {
                    Button(
                        onClick = onCheckoutClick,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCartCheckout,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(
                                id = R.string.checkout_total,
                                String.format(Locale.US, "$%.2f", state.total)
                            ),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

    if (state.itemToRemove != null) {
        RemoveCartItemDialog(
            onConfirm = { onAction(CartAction.ConfirmRemoveItem) },
            onDismiss = { onAction(CartAction.CancelDeletingItem) }
        )
    }
}
