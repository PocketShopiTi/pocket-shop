package com.iti.pocketshop.features.productdetails.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.LocalUser
import com.iti.pocketshop.R
import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.core.components.DeleteFavoriteDialogController
import com.iti.pocketshop.core.components.RemoveFavoriteDialog
import com.iti.pocketshop.core.components.ScreenStateLayout
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.features.productdetails.domain.entity.toFavoriteProduct
import com.iti.pocketshop.features.productdetails.presentation.components.EmptyProductContent
import com.iti.pocketshop.features.productdetails.presentation.components.LoadingContent
import com.iti.pocketshop.features.productdetails.presentation.components.ProductBottomBar
import com.iti.pocketshop.features.productdetails.presentation.components.ProductContent
import com.iti.pocketshop.features.productdetails.presentation.components.ProductDetailsTopAppBar
import com.iti.pocketshop.features.productdetails.presentation.components.ReviewEditorSheet
import com.iti.pocketshop.ui.theme.PocketShopTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsRoot(
    productId: String,
    onBack: () -> Unit,
    onGenerateOutfit: (title: String, productId: String) -> Unit = { _, _ -> },
    onAiCompare: (productId: String) -> Unit = {},
    onSuggestProductClick: (String) -> Unit = {},
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

    ProductDetailsScreen(
        state = state,
        scope = scope,
        currentUserId = user?.uid,
        defaultReviewCustomerName = user.defaultReviewCustomerName(),
        onAction = { action ->
            if ((user == null || user.isAnonymous) && (
                        action == ProductDetailsAction.AddToCartClicked ||
                                action == ProductDetailsAction.IncreaseQuantity ||
                                action == ProductDetailsAction.DecreaseQuantity ||
                                action == ProductDetailsAction.ToggleFavorite ||
                                action is ProductDetailsAction.WriteReviewClicked ||
                                action is ProductDetailsAction.EditReviewClicked ||
                                action is ProductDetailsAction.DeleteReviewClicked ||
                                action is ProductDetailsAction.ReviewSubmitted ||
                                action == ProductDetailsAction.DeleteReviewConfirmed
                        )
            ) {
                scope.launch { SignInDialogController.sendEvent(true) }
                return@ProductDetailsScreen
            }
            when (action) {
                ProductDetailsAction.BackClicked -> onBack()
                ProductDetailsAction.GenerateOutfitClicked -> {
                    state.product?.let { product -> onGenerateOutfit(product.title, product.id) }
                }
                ProductDetailsAction.CompareSimilarProductsClicked -> {
                    onAiCompare(productId)
                }
                is ProductDetailsAction.EditReviewClicked -> {
                    if (action.review.customerId == user?.uid) viewModel.onAction(action)
                }
                is ProductDetailsAction.DeleteReviewClicked -> {
                    if (action.review.customerId == user?.uid) viewModel.onAction(action)
                }
                is ProductDetailsAction.ReviewSubmitted -> {
                    val editingReview = state.editingReview
                    if (editingReview == null || editingReview.customerId == user?.uid) {
                        viewModel.onAction(action)
                    }
                }
                is ProductDetailsAction.SuggestedProductClicked -> {
                    onSuggestProductClick(action.productId)
                }
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun ProductDetailsScreen(
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
    currentUserId: String? = null,
    defaultReviewCustomerName: String = "",
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    // Intercept system back press when the reviews overlay is visible
    BackHandler(enabled = state.isShowingAllReviews) {
        onAction(ProductDetailsAction.HideAllReviewsClicked)
    }

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
            if (state.product != null && !state.isShowingAllReviews) {
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
            if (state.isShowingAllReviews && state.product != null) {
                ProductReviewsScreen(
                    state = state,
                    onAction = { action ->
                        if (action == ProductDetailsAction.BackClicked) {
                            onAction(ProductDetailsAction.HideAllReviewsClicked)
                        } else {
                            onAction(action)
                        }
                    },
                    currentUserId = currentUserId,
                    defaultReviewCustomerName = defaultReviewCustomerName,
                )
            } else {
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
                                currentUserId = currentUserId,
                                defaultReviewCustomerName = defaultReviewCustomerName,
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
    }

    RemoveFavoriteDialog(
        onConfirm = { onAction(ProductDetailsAction.ToggleFavorite) },
    )

    if (state.isReviewEditorVisible) {
        ReviewEditorSheet(
            editingReview = state.editingReview,
            initialCustomerName = state.reviewCustomerName,
            customerId = currentUserId.orEmpty(),
            isSubmitting = state.reviewActionInProgress,
            onAction = onAction,
        )
    }

    state.reviewToDelete?.let { _ ->
        AlertDialog(
            onDismissRequest = { onAction(ProductDetailsAction.DeleteReviewDismissed) },
            title = { Text(text = stringResource(R.string.product_details_delete_review_title)) },
            text = { Text(text = stringResource(R.string.product_details_delete_review_message)) },
            confirmButton = {
                Button(
                    onClick = { onAction(ProductDetailsAction.DeleteReviewConfirmed) },
                    enabled = !state.reviewActionInProgress,
                ) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(ProductDetailsAction.DeleteReviewDismissed) },
                    enabled = !state.reviewActionInProgress,
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

private fun UserSession?.defaultReviewCustomerName(): String {
    return this?.displayName
        ?.takeIf { it.isNotBlank() }
        ?: this?.email
            ?.substringBefore("@")
            ?.takeIf { it.isNotBlank() }
        ?: ""
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