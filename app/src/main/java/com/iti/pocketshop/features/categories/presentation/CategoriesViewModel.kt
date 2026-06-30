package com.iti.pocketshop.features.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.categories.domain.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CategoriesState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                fetchCategories()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CategoriesState()
        )

    fun onAction(action: CategoriesAction) {
        when (action) {
            is CategoriesAction.FetchCategories -> fetchCategories()
            is CategoriesAction.SearchCategories -> {
                _state.update {
                    it.copy(
                        searchQuery = action.query,
                        filteredCategories = if (action.query.isEmpty()) {
                            it.categories
                        } else {
                            it.categories.filter { category ->
                                category.title.contains(action.query, ignoreCase = true)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getCategoriesUseCase()
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            categories = data,
                            filteredCategories = if (it.searchQuery.isEmpty()) data else data.filter { category ->
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
