package com.iti.pocketshop.features.productdetails.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.LocalUser
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.features.productdetails.presentation.components.ErrorContent
import com.iti.pocketshop.features.productdetails.presentation.components.LoadingContent
import com.iti.pocketshop.features.productdetails.presentation.components.ProductBottomBar
import com.iti.pocketshop.features.productdetails.presentation.components.ProductContent
import com.iti.pocketshop.ui.theme.PocketShopTheme
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsRoot(
    productId: String,
    onBack: () -> Unit,
    onSeeAllReviews: (productId: String) -> Unit = {},
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val user = LocalUser.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        viewModel.onAction(ProductDetailsAction.ProductChanged(productId))
    }

    ProductDetailsScreen(
        state = state,
        onAction = { action ->
            if (user?.isAnonymous == true && (
                        action == ProductDetailsAction.AddToCartClicked ||
                                action == ProductDetailsAction.IncreaseQuantity ||
                                action == ProductDetailsAction.DecreaseQuantity ||
                                action == ProductDetailsAction.ToggleFavorite
                        )
            ) {
                scope.launch {
                    SignInDialogController.sendEvent(true)
                }
                return@ProductDetailsScreen
            }
            when (action) {
                ProductDetailsAction.BackClicked -> onBack()
                ProductDetailsAction.SeeAllReviewsClicked -> onSeeAllReviews(productId)
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun ProductDetailsScreen(
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
) {
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
        when {
            state.isLoading -> LoadingContent(Modifier.padding(innerPadding))
            state.errorMessage != null || state.product == null -> ErrorContent(
                modifier = Modifier.padding(innerPadding),
                onRetry = { onAction(ProductDetailsAction.Retry) },
            )

            else -> ProductContent(
                product = state.product,
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            )
        }
    }
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
                selectedOptionValueIds = mapOf("colour" to "ecru", "size" to "m"),
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
