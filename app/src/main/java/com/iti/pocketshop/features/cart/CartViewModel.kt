package com.iti.pocketshop.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.coupons.domain.usecase.ApplyCouponUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val applyCouponUseCase: ApplyCouponUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CartState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CartState()
        )

    fun onAction(action: CartAction) {
        when (action) {
            is CartAction.CartIdChanged -> {
                _state.update { state -> state.copy(cartId = action.value) }
            }

            is CartAction.CouponCodeChanged -> {
                _state.update { state -> state.copy(couponCode = action.value) }
            }

            CartAction.ApplyCouponClicked -> applyCoupon()
        }
    }

    private fun applyCoupon() {
        val currentState = _state.value
        if (!currentState.canApplyCoupon) return

        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isApplyingCoupon = true,
                    couponResult = null,
                    error = null,
                )
            }

            applyCouponUseCase(
                cartId = currentState.cartId.trim(),
                discountCodes = listOf(currentState.couponCode.trim()),
            ).onSuccess { result ->
                _state.update { state ->
                    state.copy(
                        isApplyingCoupon = false,
                        couponResult = result,
                    )
                }
            }.onError { error ->
                _state.update { state ->
                    state.copy(
                        isApplyingCoupon = false,
                        error = error,
                    )
                }
            }
        }
    }
}
