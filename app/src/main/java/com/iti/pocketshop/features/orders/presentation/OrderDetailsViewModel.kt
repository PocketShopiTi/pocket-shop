package com.iti.pocketshop.features.orders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.orders.domain.usecase.GetOrderDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getOrderDetails: GetOrderDetailsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailsState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = OrderDetailsState(),
    )

    private var loadJob: Job? = null

    fun onAction(action: OrderDetailsAction) {
        when (action) {
            is OrderDetailsAction.OrderChanged -> loadOrder(action.orderId)
            OrderDetailsAction.Retry -> loadOrder(_state.value.orderId, force = true)
        }
    }

    private fun loadOrder(orderId: String, force: Boolean = false) {
        if (orderId.isBlank()) return
        if (!force && _state.value.orderId == orderId && _state.value.order != null) return

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    orderId = orderId,
                    isLoading = true,
                    error = null,
                )
            }

            when (val result = getOrderDetails(orderId = orderId)) {
                is PocketResult.Error -> _state.update {
                    it.copy(isLoading = false, error = result.error)
                }

                is PocketResult.Success -> _state.update {
                    it.copy(
                        order = result.data,
                        isLoading = false,
                        error = null,
                    )
                }
            }
        }
    }
}
