package com.iti.pocketshop.features.orders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.orders.domain.usecase.GetOrdersPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersPage: GetOrdersPageUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var isPageRequestRunning = false

    private val _state = MutableStateFlow(OrdersState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialPage()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OrdersState(),
        )

    fun onAction(action: OrdersAction) {
        when (action) {
            OrdersAction.LoadNextPage -> loadNextPage()
            
            OrdersAction.Retry -> {
                if (_state.value.orders.isEmpty()) {
                    loadInitialPage()
                } else {
                    loadNextPage(isRetry = true)
                }
            }
        }
    }

    private fun loadInitialPage() {
        if (isPageRequestRunning) return
        isPageRequestRunning = true

        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        isInitialLoading = true,
                        hasNextPage = true,
                        endCursor = null,
                        error = null,
                    )
                }

                when (val result = getOrdersPage(pageSize = PAGE_SIZE, after = null)) {
                    is PocketResult.Error -> _state.update {
                        it.copy(isInitialLoading = false, error = result.error)
                    }

                    is PocketResult.Success -> _state.update {
                        it.copy(
                            orders = result.data.orders,
                            isInitialLoading = false,
                            hasNextPage = result.data.hasNextPage,
                            endCursor = result.data.endCursor,
                            error = null,
                        )
                    }
                }
            } finally {
                isPageRequestRunning = false
            }
        }
    }

    private fun loadNextPage(isRetry: Boolean = false) {
        val currentState = _state.value
        if (
            isPageRequestRunning ||
            currentState.isInitialLoading ||
            currentState.isLoadingMore ||
            (currentState.error != null && !isRetry) ||
            !currentState.hasNextPage
        ) {
            return
        }
        isPageRequestRunning = true

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoadingMore = true, error = null) }

                when (
                    val result = getOrdersPage(
                        pageSize = PAGE_SIZE,
                        after = currentState.endCursor,
                    )
                ) {
                    is PocketResult.Error -> _state.update {
                        it.copy(isLoadingMore = false, error = result.error)
                    }

                    is PocketResult.Success -> _state.update { state ->
                        state.copy(
                            orders = (state.orders + result.data.orders).distinctBy { it.id },
                            isLoadingMore = false,
                            hasNextPage = result.data.hasNextPage,
                            endCursor = result.data.endCursor,
                            error = null,
                        )
                    }
                }
            } finally {
                isPageRequestRunning = false
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}
