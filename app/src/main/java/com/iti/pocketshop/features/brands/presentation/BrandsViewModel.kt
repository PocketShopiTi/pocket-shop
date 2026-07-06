package com.iti.pocketshop.features.brands.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.network.onError
import com.iti.pocketshop.network.onSuccess
import com.iti.pocketshop.features.brands.domain.GetBrandsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandsViewModel @Inject constructor(
    private val getBrandsUseCase: GetBrandsUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(BrandsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                fetchBrands()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = BrandsState()
        )

    fun onAction(action: BrandsAction) {
        when (action) {
            is BrandsAction.FetchBrands -> fetchBrands()
            is BrandsAction.SearchBrands -> {
                _state.update {
                    it.copy(
                        searchQuery = action.query,
                        filteredBrands = if (action.query.isEmpty()) {
                            it.brands
                        } else {
                            it.brands.filter { brandItem ->
                                brandItem.title.contains(action.query, ignoreCase = true)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun fetchBrands() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getBrandsUseCase()
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            brands = data,
                            filteredBrands = if (it.searchQuery.isEmpty()) data else data.filter { category ->
                                category.title.contains(it.searchQuery, ignoreCase = true)
                            },
                            isLoading = false
                        )
                    }
                }
                .onError {
                    _state.update { it.copy(isLoading = false) }
                    ErrorDialogController.sendEvent(it)
                }
        }
    }
}
