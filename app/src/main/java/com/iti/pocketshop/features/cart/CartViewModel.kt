package com.iti.pocketshop.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.usecase.GetLocalCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RemoveCartItemUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RestoreOrCreateCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.UpdateCartQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CartViewModel @Inject constructor(
    private val restoreOrCreateCartUseCase: RestoreOrCreateCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    getLocalCartUseCase: GetLocalCartUseCase,
) : ViewModel() {

    private var loadedInitialData = false
    private var updateQuantityJob: Job? = null
    private val _state = MutableStateFlow(CartState())

    val state = _state
        .combine(
            getLocalCartUseCase(),
        ) { currentState, localCart ->
            val items = localCart?.lines ?: emptyList()
            currentState.copy(
                cartId = localCart?.id ?: currentState.cartId,
                items = items,
                subTotal = localCart?.subtotalAmount?.amount ?: 0.0,
                currencyCode = localCart?.totalAmount?.currencyCode?.name ?: items.firstOrNull()?.currencyCode ?: "",
                itemsCounts = localCart?.totalQuantity ?: 0,
                total = localCart?.totalAmount?.amount ?: 0.0,
                appliedDiscountCodes = localCart?.appliedDiscountCodes ?: emptyList(),
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
        updateQuantityJob?.cancel()
        updateQuantityJob = viewModelScope.launch {
            delay(500L.milliseconds)
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
                .onSuccess { cart ->
                    _state.update {
                        it.copy(
                            cartId = cart.id,
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