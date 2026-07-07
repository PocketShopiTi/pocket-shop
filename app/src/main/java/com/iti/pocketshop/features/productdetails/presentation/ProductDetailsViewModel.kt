package com.iti.pocketshop.features.productdetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.usecase.IsFavoriteUseCase
import com.iti.pocketshop.common.favorites.domain.usecase.ToggleFavoriteUseCase
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.cart.domain.usecase.AddToCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RestoreOrCreateCartUseCase
import com.iti.pocketshop.features.evaluate.domain.usecase.DeleteProductReviewUseCase
import com.iti.pocketshop.features.evaluate.domain.usecase.SubmitProductReviewUseCase
import com.iti.pocketshop.features.evaluate.domain.usecase.UpdateProductReviewUseCase
import com.iti.pocketshop.features.productdetails.data.mapper.parseReviewDate
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import com.iti.pocketshop.features.productdetails.domain.entity.toFavoriteProduct
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round

@HiltViewModel(assistedFactory = ProductDetailsViewModel.Factory::class)
class ProductDetailsViewModel @AssistedInject constructor(
    private val getProductDetails: GetProductDetailsUseCase,
    private val isFavorite: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val restoreOrCreateCartUseCase: RestoreOrCreateCartUseCase,
    private val submitProductReviewUseCase: SubmitProductReviewUseCase,
    private val updateProductReviewUseCase: UpdateProductReviewUseCase,
    private val deleteProductReviewUseCase: DeleteProductReviewUseCase,
    @Assisted private val productId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(productId: String): ProductDetailsViewModel
    }

    private var hasLoadedInitialData = false
    private var loadJob: Job? = null
    private var cartFeedbackJob: Job? = null
    private var addToCartJob: Job? = null
    private var reviewJob: Job? = null

    private val _state = MutableStateFlow(ProductDetailsState(productId = productId))
    val state = combine(_state, isFavorite(productId)) { state, favorite ->
        state.copy(isFavorite = favorite)
    }.onStart {
        if (!hasLoadedInitialData) {
            loadProduct()
            fetchCart()
            hasLoadedInitialData = true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProductDetailsState(productId = productId),
    )

    private val _events = Channel<ProductDetailsEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ProductDetailsAction) {
        when (action) {
            is ProductDetailsAction.ImageSelected -> selectImage(action.index)
            is ProductDetailsAction.OptionSelected -> selectOption(action.optionId, action.valueId)
            ProductDetailsAction.ToggleFavorite -> toggleFavorite()
            ProductDetailsAction.ToggleDescription -> _state.update { state ->
                state.copy(isDescriptionExpanded = !state.isDescriptionExpanded)
            }
            ProductDetailsAction.DecreaseQuantity -> _state.update { state ->
                state.copy(quantity = (state.quantity - 1).coerceAtLeast(1))
            }
            ProductDetailsAction.IncreaseQuantity -> _state.update { state ->
                state.copy(quantity = (state.quantity + 1).coerceAtMost(MAX_QUANTITY))
            }
            ProductDetailsAction.Retry -> loadProduct()
            ProductDetailsAction.AddToCartClicked -> showAddToCartFeedback()
            ProductDetailsAction.SeeAllReviewsClicked -> _state.update { state ->
                state.copy(isShowingAllReviews = true)
            }
            ProductDetailsAction.HideAllReviewsClicked -> _state.update { state ->
                state.copy(isShowingAllReviews = false)
            }
            is ProductDetailsAction.WriteReviewClicked -> _state.update { state ->
                state.copy(
                    isReviewEditorVisible = true,
                    editingReview = null,
                    reviewCustomerName = action.defaultCustomerName,
                )
            }
            is ProductDetailsAction.EditReviewClicked -> _state.update { state ->
                state.copy(
                    isReviewEditorVisible = true,
                    editingReview = action.review,
                    reviewCustomerName = action.review.author,
                )
            }
            ProductDetailsAction.ReviewEditorDismissed -> _state.update { state ->
                if (state.reviewActionInProgress) {
                    state
                } else {
                    state.copy(
                        isReviewEditorVisible = false,
                        editingReview = null,
                        reviewCustomerName = "",
                    )
                }
            }
            is ProductDetailsAction.ReviewSubmitted -> submitReview(action)
            is ProductDetailsAction.DeleteReviewClicked -> _state.update { state ->
                state.copy(reviewToDelete = action.review)
            }
            ProductDetailsAction.DeleteReviewDismissed -> _state.update { state ->
                if (state.reviewActionInProgress) state else state.copy(reviewToDelete = null)
            }
            ProductDetailsAction.DeleteReviewConfirmed -> deleteSelectedReview()
            ProductDetailsAction.BackClicked -> Unit
        }
    }

    private fun loadProduct() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { state -> state.copy(isLoading = true, error = null) }
            getProductDetails(productId)
                .onSuccess(::showProduct)
                .onError { error ->
                    _state.update { state -> state.copy(isLoading = false, error = error) }
                }
        }
    }

    private fun showProduct(product: ProductDetails) {
        val defaultVariant = product.defaultVariant
        val selectedImageIndex = product.images.indexOfFirst { image ->
            image.url == defaultVariant?.imageUrl
        }.coerceAtLeast(0)

        _state.update { state ->
            ProductDetailsState(
                productId = state.productId,
                product = product,
                selectedImageIndex = selectedImageIndex,
                selectedOptionValueIds = defaultVariant?.selectedOptionValueIds.orEmpty(),
                isFavorite = state.isFavorite,
                cartId = state.cartId,
                isLoading = false,
            )
        }
    }

    private fun selectImage(index: Int) {
        _state.update { state ->
            state.copy(
                selectedImageIndex = index.coerceIn(
                    minimumValue = 0,
                    maximumValue = state.galleryImages.lastIndex.coerceAtLeast(0),
                ),
            )
        }
    }

    private fun selectOption(optionId: String, valueId: String) {
        _state.update { state ->
            val product = state.product ?: return@update state
            val variant = product.resolveAvailableVariant(
                optionId = optionId,
                valueId = valueId,
                currentSelections = state.selectedOptionValueIds,
            ) ?: return@update state
            val variantImageIndex = product.images.indexOfFirst { image ->
                image.url == variant.imageUrl
            }
            state.copy(
                selectedOptionValueIds = variant.selectedOptionValueIds,
                // Animate the pager to the variant's own image when it has one.
                selectedImageIndex = if (variantImageIndex >= 0) {
                    variantImageIndex
                } else {
                    state.selectedImageIndex
                },
                isAddedToCart = false,
            )
        }
    }

    private fun toggleFavorite() {
        val product = _state.value.product ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(product.toFavoriteProduct())
                .onError { error -> _events.send(ProductDetailsEvent.ShowError(error)) }
        }
    }

    private fun showAddToCartFeedback() {
        val state = _state.value
        val variant = state.selectedVariant
        if (variant?.availableForSale != true) return

        val cartId = state.cartId ?: run {
            viewModelScope.launch {
                _events.send(ProductDetailsEvent.ShowError(PocketDataError.Remote.UNKNOWN))
                restoreOrCreateCartUseCase()
                    .onSuccess { cart ->
                        _state.update { current -> current.copy(cartId = cart.id) }
                    }
            }
            return
        }

        addToCartJob?.cancel()
        addToCartJob = viewModelScope.launch {
            delay(ADD_TO_CART_DEBOUNCE_MILLIS)
            addToCartUseCase(
                cartId = cartId,
                variantId = variant.id,
                quantity = state.quantity,
            )
        }

        _state.update { current -> current.copy(isAddedToCart = true) }
        cartFeedbackJob?.cancel()
        cartFeedbackJob = viewModelScope.launch {
            delay(CART_FEEDBACK_DURATION_MILLIS)
            _state.update { current -> current.copy(isAddedToCart = false) }
        }
    }

    private fun fetchCart() {
        viewModelScope.launch {
            restoreOrCreateCartUseCase()
                .onSuccess { cart ->
                    _state.update { current -> current.copy(cartId = cart.id) }
                }
        }
    }

    private fun submitReview(action: ProductDetailsAction.ReviewSubmitted) {
        val currentState = _state.value
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
                    .onError { _events.send(ProductDetailsEvent.ShowError(it)) }
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
                    .onError { _events.send(ProductDetailsEvent.ShowError(it)) }
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
        val review = _state.value.reviewToDelete ?: return
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
                .onError { _events.send(ProductDetailsEvent.ShowError(it)) }
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
            current.copy(
                product = product.withReviews(product.reviews.filterNot { it.id == reviewId }),
            )
        }
    }

    private companion object {
        const val MAX_QUANTITY = 99
        const val CART_FEEDBACK_DURATION_MILLIS = 1_200L
        const val ADD_TO_CART_DEBOUNCE_MILLIS = 300L
    }
}

private fun ProductDetails.withReviews(reviews: List<ProductReview>): ProductDetails {
    val rating = if (reviews.isEmpty()) {
        0.0
    } else {
        round(reviews.map { it.rating }.average() * 10.0) / 10.0
    }
    return copy(
        reviews = reviews,
        reviewCount = reviews.size,
        rating = rating,
    )
}
