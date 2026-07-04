package com.iti.pocketshop.features.productdetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.usecase.IsFavoriteUseCase
import com.iti.pocketshop.common.favorites.domain.usecase.ToggleFavoriteUseCase
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetails: GetProductDetailsUseCase,
    private val isFavorite: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val cartRepository: CartRepository
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), ProductDetailsState())

    private var loadJob: Job? = null
    private var cartFeedbackJob: Job? = null

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
            is ProductDetailsAction.ToggleFavorite -> {
                toggleFavorite(action.product)
            }
            else -> _state.update { current -> reduceProductDetails(current, action) }
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
        
        if (variant != null) {
            viewModelScope.launch {
                val cartId = cartRepository.cartState.value?.id ?: return@launch
                cartRepository.addLines(
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

    private companion object {
        const val CART_FEEDBACK_DURATION_MILLIS = 1_200L
    }
}
