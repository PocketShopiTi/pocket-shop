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
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
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

@HiltViewModel(assistedFactory = ProductDetailsViewModel.Factory::class)
class ProductDetailsViewModel @AssistedInject constructor(
    private val getProductDetails: GetProductDetailsUseCase,
    private val isFavorite: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val restoreOrCreateCartUseCase: RestoreOrCreateCartUseCase,
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

    private companion object {
        const val MAX_QUANTITY = 99
        const val CART_FEEDBACK_DURATION_MILLIS = 1_200L
        const val ADD_TO_CART_DEBOUNCE_MILLIS = 300L
    }
}
