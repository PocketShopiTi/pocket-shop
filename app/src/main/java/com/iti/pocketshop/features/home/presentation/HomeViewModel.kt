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
import com.iti.pocketshop.features.home.presentation.models.toUIProduct
import com.iti.pocketshop.features.home.domain.GetPromotionAdsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
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
            val favoriteIds = favorites.map(FavoriteProduct::id).toSet()
            state.copy(
                favoriteIds = favoriteIds,
                featuredProducts = state.featuredProducts.map { it.copy(isFavorite = favoriteIds.contains(it.id)) },
                bestSellers = state.bestSellers.map { it.copy(isFavorite = favoriteIds.contains(it.id)) },
                newArrivals = state.newArrivals.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
            )
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
            val job1 = launch {
                getHomeDataUseCase()
                    .onSuccess { data ->
                        _state.update { current ->
                            current.copy(
                                isEmptyState = data.brands.isEmpty() && data.featuredProducts.isEmpty() && data.bestSellers.isEmpty() && data.newArrivals.isEmpty(),
                                brands = data.brands,
                                categories = data.categories,
                                featuredProducts = data.featuredProducts.map { it.toUIProduct(current.favoriteIds.contains(it.id)) },
                                bestSellers = data.bestSellers.map { it.toUIProduct(current.favoriteIds.contains(it.id)) },
                                newArrivals = data.newArrivals.map { it.toUIProduct(current.favoriteIds.contains(it.id)) }
                            )
                        }
                    }
                    .onError { error ->
                        _state.update { it.copy(isLoading = false) }
                        ErrorDialogController.sendEvent(error)
                    }
            }

            val job2 = launch {
                getPromotionAdsUseCase()
                    .onSuccess { ads ->
                        _state.update { it.copy(promotionAds = ads) }
                    }
                    .onError {
                        ErrorDialogController.sendEvent(it)
                    }
            }
            joinAll(job1, job2)

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
