package com.iti.pocketshop.features.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.features.cart.components.CartEmptyState
import com.iti.pocketshop.features.cart.components.CartItemCard
import com.iti.pocketshop.features.cart.components.OrderSummaryCard
import com.iti.pocketshop.features.cart.components.RemoveCartItemDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import java.util.Locale

@Composable
fun CartRoot(
    viewModel: CartViewModel = hiltViewModel(),
    onStartShoppingClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CartScreen(
        state = state,
        onAction = { action ->
            if (action == CartAction.StartShoppingClicked) {
                onStartShoppingClick()
            } else {
                viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartState,
    onAction: (CartAction) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(state.checkoutUrl) {
        state.checkoutUrl?.let { url ->
            uriHandler.openUri(url)
            onAction(CartAction.CheckoutHandled)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            ErrorDialogController.sendEvent(error)
            onAction(CartAction.ErrorHandled)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.cart),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                windowInsets = TopAppBarDefaults.windowInsets.exclude(WindowInsets.statusBars),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.items.size.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { onAction(CartAction.CheckoutClicked) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cart),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.checkout_total, String.format(Locale.US, "$%.2f", state.total)),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.items.isEmpty()) {
            CartEmptyState(
                onStartShoppingClick = { onAction(CartAction.StartShoppingClicked) },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                if (state.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                items(state.items, key = { it.lineId }) { item ->
                    CartItemCard(
                        item = item,
                        onRemoveClick = { onAction(CartAction.RemoveItemClicked(item)) },
                        onUpdateQuantity = { qty -> onAction(CartAction.UpdateQuantity(item.lineId, qty)) }
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

    if (state.itemToRemove != null) {
        RemoveCartItemDialog(
            onConfirm = { onAction(CartAction.ConfirmRemoveItem) },
            onDismiss = { onAction(CartAction.CancelRemoveItem) }
        )
    }
}
