package com.iti.pocketshop.features.aicompare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.home.domain.models.Money
import com.iti.pocketshop.features.home.domain.models.Product
import com.iti.pocketshop.features.productdetails.domain.usecase.CompareProductsAiUseCase
import com.iti.pocketshop.features.productdetails.domain.usecase.GetProductDetailsUseCase
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsAction
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.features.search.domain.usecase.GetSearchResultsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AiCompareViewModel.Factory::class)
class AiCompareViewModel @AssistedInject constructor(
    private val getProductDetails: GetProductDetailsUseCase,
    private val getSearchResultsUseCase: GetSearchResultsUseCase,
    private val compareProductsAiUseCase: CompareProductsAiUseCase,
    @Assisted private val productId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(productId: String): AiCompareViewModel
    }

    private val _state = MutableStateFlow(AiCompareState(productId = productId))
    val state = _state.asStateFlow()

    private var aiSearchJob: Job? = null
    private var aiCompareJob: Job? = null

    init {
        loadProductAndSimilar()
    }

    fun onAction(action: ProductDetailsAction) {
        when (action) {
            is ProductDetailsAction.ToggleSimilarProductSelection -> toggleSimilarProductSelection(
                action.product
            )

            ProductDetailsAction.CompareSelectedProductsClicked -> compareSelectedProducts()
            ProductDetailsAction.Retry -> loadProductAndSimilar()
            else -> Unit
        }
    }

    private fun loadProductAndSimilar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProductDetails(productId)
                .onSuccess { product ->
                    _state.update { it.copy(product = product, isLoading = false) }
                    loadSimilarProducts(product.title)
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                }
        }
    }

    private fun loadSimilarProducts(query: String) {
        aiSearchJob?.cancel()
        aiSearchJob = viewModelScope.launch {
            _state.update { it.copy(isSearchingSimilar = true) }
            getSearchResultsUseCase(query = query)
                .onSuccess { results ->
                    val products = results.items.filterIsInstance<SearchResultItem.ProductItem>()
                        .filter { p -> p.id != productId }
                        .map { item ->
                            Product(
                                id = item.id,
                                title = item.title,
                                handle = item.handle,
                                vendor = item.vendor,
                                availableForSale = true,
                                price = Money(item.price, item.currencyCode),
                                compareAtPrice = null,
                                imageUrl = item.imageUrl,
                                imageAlt = item.imageAlt
                            )
                        }
                        .take(8)

                    _state.update {
                        it.copy(
                            isSearchingSimilar = false,
                            similarProducts = products
                        )
                    }
                }
                .onError {
                    _state.update { it.copy(isSearchingSimilar = false) }
                    viewModelScope.launch {
                        ErrorDialogController.sendEvent(it)
                    }
                }
        }
    }

    private fun toggleSimilarProductSelection(product: Product) {
        _state.update { state ->
            val currentSelected = state.selectedProductsToCompare.toMutableList()
            if (currentSelected.any { it.id == product.id }) {
                currentSelected.removeAll { it.id == product.id }
            } else {
                if (currentSelected.size < 4) {
                    currentSelected.add(product)
                }
            }
            state.copy(selectedProductsToCompare = currentSelected)
        }
    }

    private fun compareSelectedProducts() {
        val currentProduct = _state.value.product ?: return
        val selectedProducts = _state.value.selectedProductsToCompare
        if (selectedProducts.isEmpty()) return

        aiCompareJob?.cancel()
        aiCompareJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isComparingWithAi = true,
                    aiComparisonResult = null
                )
            }

            compareProductsAiUseCase(currentProduct, selectedProducts)
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            isComparingWithAi = false,
                            aiComparisonResult = result
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isComparingWithAi = false) }
                    viewModelScope.launch {
                        ErrorDialogController.sendEvent(PocketDataError.Remote.UNKNOWN)
                    }
                }
        }
    }
}
