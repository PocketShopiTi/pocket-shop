package com.iti.pocketshop.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.usecase.GetLocalFavoritesUseCase
import com.iti.pocketshop.common.favorites.domain.usecase.ToggleFavoriteUseCase
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.home.domain.GetHomeDataUseCase
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
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    getLocalFavoritesUseCase: GetLocalFavoritesUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .combine(getLocalFavoritesUseCase()) { state, favorites ->
            state.copy(favoriteIds = favorites.map(FavoriteProduct::id).toSet())
        }
        .onStart {
            if (!hasLoadedInitialData) {
                fetchHomeData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeState()
        )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.FetchData -> fetchHomeData()
            is HomeAction.ToggleFavorite -> toggleFavorite(action.product)
        }
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            getHomeDataUseCase()
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            brands = data.brands,
                            featuredProducts = data.featuredProducts,
                            bestSellers = data.bestSellers,
                            newArrivals = data.newArrivals
                        )
                    }
                }
                .onError {
                    ErrorDialogController.sendEvent(it)
                }
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
}