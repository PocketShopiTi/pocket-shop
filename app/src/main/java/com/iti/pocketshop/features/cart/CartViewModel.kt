package com.iti.pocketshop.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.usecase.GetLocalCartItemsUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RemoveCartItemUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RestoreOrCreateCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.UpdateCartQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val restoreOrCreateCartUseCase: RestoreOrCreateCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    getLocalCartItemsUseCase: GetLocalCartItemsUseCase,
) : ViewModel() {

    private var loadedInitialData = false
    private val _state = MutableStateFlow(CartState())

    val state = _state
        .combine(
            getLocalCartItemsUseCase(),
        ) { currentState, localItems ->
            currentState.copy(
                items = localItems,
                subTotal = localItems.sumOf { it.price * it.quantity },
                currencyCode = localItems.firstOrNull()?.currencyCode ?: "",
                shipping = 0.0,
                itemsCounts = localItems.groupBy { it.variantId }.mapValues { it.value.size }.values.sum(),
                total = localItems.sumOf { it.price * it.quantity }
            )
        }
        .onStart {
            if (!loadedInitialData) {
                fetchCart()
                loadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CartState()
        )

    fun onAction(action: CartAction) {
        when (action) {
            CartAction.FetchCart -> fetchCart()
            is CartAction.UpdateQuantity -> updateItemQuantity(action)
            is CartAction.PrepareDeletingItem -> prepareItemForDeletion(action.item)
            CartAction.ConfirmRemoveItem -> confirmDeleteSelectedItem()
            CartAction.CancelDeletingItem -> cancelDeletingItem()
        }
    }

    private fun confirmDeleteSelectedItem() {
        viewModelScope.launch {
            val item = _state.value.itemToRemove ?: return@launch
            val cartId = state.value.cartId ?: return@launch
            _state.update {
                it.copy(
                    itemToRemove = null,
                    isLoading = true,
                )
            }
            removeCartItemUseCase(cartId, item.lineId)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun cancelDeletingItem() {
        _state.update { it.copy(itemToRemove = null) }
    }

    private fun prepareItemForDeletion(item: CartLineItem) {
        _state.update { it.copy(itemToRemove = item) }
    }

    private fun updateItemQuantity(action: CartAction.UpdateQuantity) {
        viewModelScope.launch {
            val cartId = state.value.cartId ?: return@launch
            _state.update { it.copy(isLoading = true) }
            updateCartQuantityUseCase(cartId, action.lineId, action.quantity)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }


    fun fetchCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            restoreOrCreateCartUseCase()
                .onSuccess {
                    _state.update {
                        it.copy(
                            cartId = it.cartId,
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }
}