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
import com.iti.pocketshop.features.home.domain.GetPromotionAdsUseCase
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
    private val getPromotionAdsUseCase: GetPromotionAdsUseCase,
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
            is HomeAction.OpenPromotionAd -> onboardingPromotionAd(action)
            HomeAction.ClosePromotionAd -> onClosePromotionAd()

        }
    }

    private fun onClosePromotionAd() {
        _state.update { it.copy(selectedPromotionAd = null) }
    }

    private fun onboardingPromotionAd(action: HomeAction.OpenPromotionAd) {
        _state.update { it.copy(selectedPromotionAd = action.ad) }
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getHomeDataUseCase()
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            categories = data.categories,
                            featuredProducts = data.featuredProducts,
                            bestSellers = data.bestSellers,
                            newArrivals = data.newArrivals
                        )
                    }
                }
                .onError {
                    ErrorDialogController.sendEvent(it)
                }

            getPromotionAdsUseCase()
                .onSuccess { ads ->
                    _state.update { it.copy(promotionAds = ads) }
                }
                .onError {
                    ErrorDialogController.sendEvent(it)
                }

            _state.update { it.copy(isLoading = false) }
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
