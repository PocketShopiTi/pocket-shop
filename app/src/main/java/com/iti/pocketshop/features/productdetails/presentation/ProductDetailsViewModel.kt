package com.iti.pocketshop.features.productdetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.usecase.IsFavoriteUseCase
import com.iti.pocketshop.common.favorites.domain.usecase.ToggleFavoriteUseCase
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import com.iti.pocketshop.features.cart.domain.usecase.AddToCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RestoreOrCreateCartUseCase
import com.iti.pocketshop.features.evaluate.domain.usecase.DeleteProductReviewUseCase
import com.iti.pocketshop.features.evaluate.domain.usecase.SubmitProductReviewUseCase
import com.iti.pocketshop.features.evaluate.domain.usecase.UpdateProductReviewUseCase
import com.iti.pocketshop.features.productdetails.data.mapper.parseReviewDate
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetails: GetProductDetailsUseCase,
    private val isFavorite: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val restoreOrCreateCartUseCase: RestoreOrCreateCartUseCase,
    private val submitProductReviewUseCase: SubmitProductReviewUseCase,
    private val updateProductReviewUseCase: UpdateProductReviewUseCase,
    private val deleteProductReviewUseCase: DeleteProductReviewUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _state
        .flatMapLatest { state ->
            isFavorite(state.productId)
                .map {
                    state.copy(isFavorite = it)
                }
        }
        .onStart {
            fetchCart()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), ProductDetailsState())

    private var loadJob: Job? = null
    private var cartFeedbackJob: Job? = null
    private var addToCartJob: Job? = null
    private var reviewJob: Job? = null

    private fun loadProduct(productId: String, force: Boolean = false) {
        if (!force && _state.value.productId == productId && _state.value.product != null) return

        _state.update { current ->
            current.copy(
                productId = productId,
                isLoading = true,
                errorMessage = null,
            )
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            getProductDetails(productId)
                .onSuccess { product ->
                    if (_state.value.productId == productId) showProduct(product)
                }
                .onFailure { error ->
                    if (_state.value.productId != productId) return@onFailure
                    _state.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = error.message ?: error::class.simpleName.orEmpty(),
                        )
                    }
                }
        }
    }

    private fun showProduct(product: ProductDetails) {
        val selectedOptions = product.options.mapNotNull { option ->
            val selectedValue = option.values.firstOrNull { value ->
                value.id in product.defaultVariant?.selectedOptionValueIds.orEmpty()
            } ?: option.values.firstOrNull()

            selectedValue?.let { option.id to it.id }
        }.toMap()

        _state.value = ProductDetailsState(
            productId = _state.value.productId,
            product = product,
            selectedOptionValueIds = selectedOptions,
            isFavorite = product.isFavorite,
            isLoading = false,
        )
    }

    fun onAction(action: ProductDetailsAction) {
        when (action) {
            is ProductDetailsAction.ProductChanged -> loadProduct(action.productId)
            ProductDetailsAction.Retry -> loadProduct(_state.value.productId, force = true)
            ProductDetailsAction.AddToCartClicked -> showAddToCartFeedback()
            is ProductDetailsAction.ReviewSubmitted -> submitReview(action)
            ProductDetailsAction.DeleteReviewConfirmed -> deleteSelectedReview()
            ProductDetailsAction.SeeAllReviewsClicked -> {
                _state.update { it.copy(isShowingAllReviews = true) }
            }
            ProductDetailsAction.HideAllReviewsClicked -> {
                _state.update { it.copy(isShowingAllReviews = false) }
            }
            is ProductDetailsAction.ToggleFavorite -> {
                toggleFavorite(action.product)
            }
            else -> _state.update { current -> reduceProductDetails(current, action) }
        }
    }

    private fun submitReview(action: ProductDetailsAction.ReviewSubmitted) {
        val currentState = _state.value
        val productId = currentState.productId
        val editingReview = currentState.editingReview
        if (productId.isBlank() || action.customerId.isBlank()) return

        reviewJob?.cancel()
        reviewJob = viewModelScope.launch {
            _state.update { it.copy(reviewActionInProgress = true) }
            var succeeded = false
            if (editingReview == null) {
                submitProductReviewUseCase(
                    productId = productId,
                    customerId = action.customerId,
                    customerName = action.customerName,
                    rating = action.rating,
                    title = action.title,
                    body = action.body,
                )
                    .onSuccess { review ->
                        val productReview = ProductReview(
                            id = review.id,
                            author = review.customerName,
                            avatarUrl = null,
                            rating = review.rating,
                            date = parseReviewDate(review.createdAt, review.createdAt),
                            body = review.body,
                            title = review.title,
                            customerId = review.customerId,
                        )
                        upsertReview(productReview)
                        succeeded = true
                    }
                    .onError { ErrorDialogController.sendEvent(it) }
            } else {
                updateProductReviewUseCase(
                    reviewId = editingReview.id,
                    customerName = action.customerName,
                    rating = action.rating,
                    title = action.title,
                    body = action.body,
                )
                    .onSuccess {
                        upsertReview(
                            editingReview.copy(
                                author = action.customerName.trim(),
                                rating = action.rating,
                                title = action.title.trim(),
                                body = action.body.trim(),
                            ),
                        )
                        succeeded = true
                    }
                    .onError { ErrorDialogController.sendEvent(it) }
            }
            _state.update {
                if (succeeded) {
                    it.copy(
                        reviewActionInProgress = false,
                        isReviewEditorVisible = false,
                        editingReview = null,
                        reviewCustomerName = "",
                    )
                } else {
                    it.copy(reviewActionInProgress = false)
                }
            }
        }
    }

    private fun deleteSelectedReview() {
        val currentState = _state.value
        val productId = currentState.productId
        val review = currentState.reviewToDelete ?: return
        if (productId.isBlank()) return

        reviewJob?.cancel()
        reviewJob = viewModelScope.launch {
            _state.update { it.copy(reviewActionInProgress = true) }
            var succeeded = false
            deleteProductReviewUseCase(productId = productId, reviewId = review.id)
                .onSuccess {
                    removeReview(review.id)
                    succeeded = true
                }
                .onError { ErrorDialogController.sendEvent(it) }
            _state.update {
                if (succeeded) {
                    it.copy(
                        reviewActionInProgress = false,
                        reviewToDelete = null,
                    )
                } else {
                    it.copy(reviewActionInProgress = false)
                }
            }
        }
    }

    private fun upsertReview(review: ProductReview) {
        _state.update { current ->
            val product = current.product ?: return@update current
            val reviews = buildList {
                add(review)
                addAll(product.reviews.filterNot { it.id == review.id })
            }
            current.copy(product = product.withReviews(reviews))
        }
    }

    private fun removeReview(reviewId: String) {
        _state.update { current ->
            val product = current.product ?: return@update current
            current.copy(product = product.withReviews(product.reviews.filterNot { it.id == reviewId }))
        }
    }

    private fun toggleFavorite(product: FavoriteProduct) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product)
                .onError {
                    ErrorDialogController.sendEvent(it)
                }
        }
    }

    private fun showAddToCartFeedback() {
        if (_state.value.selectedVariant?.availableForSale != true) return

        val currentState = _state.value
        val variant = currentState.selectedVariant
        val cartId = _state.value.cartId ?: run {
            viewModelScope.launch {
                ErrorDialogController.sendEvent(PocketDataError.Remote.UNKNOWN)
                restoreOrCreateCartUseCase()
            }
            return
        }
        if (variant != null) {
            addToCartJob?.cancel()
            addToCartJob = viewModelScope.launch {
                delay(300L.milliseconds)
                addToCartUseCase(
                    cartId = cartId,
                    variantId = variant.id,
                    quantity = currentState.quantity
                )
            }
        }

        _state.update { current ->
            reduceProductDetails(current, ProductDetailsAction.AddToCartClicked)
        }
        cartFeedbackJob?.cancel()
        cartFeedbackJob = viewModelScope.launch {
            delay(CART_FEEDBACK_DURATION_MILLIS.milliseconds)
            _state.update { current ->
                reduceProductDetails(current, ProductDetailsAction.CartFeedbackFinished)
            }
        }
    }

    private fun fetchCart() {
        viewModelScope.launch {
            restoreOrCreateCartUseCase()
                .onSuccess {
                    _state.update { current ->
                        current.copy(
                            cartId = it.id
                        )
                    }
                }
        }
    }

    private companion object {
        const val CART_FEEDBACK_DURATION_MILLIS = 1_200L
    }
}

private fun ProductDetails.withReviews(reviews: List<ProductReview>): ProductDetails {
    val rating = if (reviews.isEmpty()) 0.0 else round(reviews.map { it.rating }.average() * 10.0) / 10.0
    return copy(
        reviews = reviews,
        reviewCount = reviews.size,
        rating = rating,
    )
}
