package com.iti.pocketshop.features.productdetails.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.LocalUser
import com.iti.pocketshop.core.components.DeleteFavoriteDialogController
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.components.RemoveFavoriteDialog
import com.iti.pocketshop.core.components.ScreenStateLayout
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.features.productdetails.domain.entity.toFavoriteProduct
import com.iti.pocketshop.features.productdetails.presentation.components.EmptyProductContent
import com.iti.pocketshop.features.productdetails.presentation.components.LoadingContent
import com.iti.pocketshop.features.productdetails.presentation.components.ProductBottomBar
import com.iti.pocketshop.features.productdetails.presentation.components.ProductContent
import com.iti.pocketshop.features.productdetails.presentation.components.ProductDetailsTopAppBar
import com.iti.pocketshop.ui.theme.PocketShopTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsRoot(
    productId: String,
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(
        key = productId,
        creationCallback = { factory: ProductDetailsViewModel.Factory ->
            factory.create(productId)
        },
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user = LocalUser.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductDetailsEvent.ShowError -> ErrorDialogController.sendEvent(event.error)
            }
        }
    }

    ProductDetailsScreen(
        state = state,
        scope = scope,
        onAction = { action ->
            if (user?.isAnonymous == true && (
                        action == ProductDetailsAction.AddToCartClicked ||
                                action == ProductDetailsAction.IncreaseQuantity ||
                                action == ProductDetailsAction.DecreaseQuantity ||
                                action == ProductDetailsAction.ToggleFavorite
                        )
            ) {
                scope.launch { SignInDialogController.sendEvent(true) }
                return@ProductDetailsScreen
            }
            when (action) {
                ProductDetailsAction.BackClicked -> onBack()
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun ProductDetailsScreen(
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    val listState = rememberLazyListState()

    val onFavoriteClick: () -> Unit = {
        state.product?.let { product ->
            if (state.isFavorite) {
                scope.launch {
                    DeleteFavoriteDialogController.sendEvent(product.toFavoriteProduct())
                }
            } else {
                onAction(ProductDetailsAction.ToggleFavorite)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (state.product != null) {
                ProductBottomBar(
                    quantity = state.quantity,
                    totalPrice = state.totalPrice,
                    isEnabled = state.selectedVariant?.availableForSale == true,
                    isAddedToCart = state.isAddedToCart,
                    onAction = onAction,
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenStateLayout(
                isLoading = state.isLoading,
                error = state.error,
                isEmpty = state.product == null,
                onRetry = { onAction(ProductDetailsAction.Retry) },
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                loadingContent = { LoadingContent() },
                emptyContent = {
                    EmptyProductContent(onRetry = { onAction(ProductDetailsAction.Retry) })
                },
                content = {
                    state.product?.let { product ->
                        ProductContent(
                            product = product,
                            state = state,
                            onAction = onAction,
                            listState = listState,
                        )
                    }
                },
            )
            ProductDetailsTopAppBar(
                isFavorite = state.isFavorite,
                favoriteEnabled = state.product != null,
                onBack = { onAction(ProductDetailsAction.BackClicked) },
                onFavoriteClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }

    RemoveFavoriteDialog(
        onConfirm = { onAction(ProductDetailsAction.ToggleFavorite) },
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsPreview() {
    val product = ProductDetailsMockData.create("preview-product")
    PocketShopTheme(isDarkTheme = false) {
        ProductDetailsScreen(
            state = ProductDetailsState(
                productId = product.id,
                product = product,
                selectedOptionValueIds = product.defaultVariant?.selectedOptionValueIds.orEmpty(),
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
