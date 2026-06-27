package com.iti.pocketshop.features.productdetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetails: GetProductDetailsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())
    val state = _state.asStateFlow()
    private var loadJob: Job? = null

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
        val defaultVariant = product.variants.firstOrNull { it.availableForSale }
            ?: product.variants.firstOrNull()
        val selectedOptions = product.options.mapNotNull { option ->
            val selectedValue = option.values.firstOrNull { value ->
                value.id in defaultVariant?.selectedOptionValueIds.orEmpty()
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
            else -> _state.update { current -> reduceProductDetails(current, action) }
        }
    }
}
