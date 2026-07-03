package com.iti.pocketshop.features.wishlist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.usecase.GetLocalFavoritesUseCase
import com.iti.pocketshop.common.favorites.domain.usecase.SyncFavoritesUseCase
import com.iti.pocketshop.common.favorites.domain.usecase.ToggleFavoriteUseCase
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.wishlist.presentation.action.WishlistAction
import com.iti.pocketshop.features.wishlist.presentation.state.WishlistState
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
class WishlistViewModel @Inject constructor(
    private val syncFavoritesUseCase: SyncFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    getLocalFavoritesUseCase: GetLocalFavoritesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(WishlistState())
    val state = _state
        .combine(
            getLocalFavoritesUseCase()
        ) { state, favorites ->
            state.copy(favorites = favorites)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = WishlistState()
        )

    fun onAction(action: WishlistAction) {
        when (action) {
            WishlistAction.FetchFavorites -> syncFavorites()
            is WishlistAction.ToggleFavorite -> toggleFavorite(action.product)
        }
    }

    private fun toggleFavorite(product: FavoriteProduct) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product)
                .onSuccess {

                }
                .onError { pocketDataError ->
                    ErrorDialogController.sendEvent(pocketDataError)
                }
        }
    }

    private fun syncFavorites() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            syncFavoritesUseCase()
                .onSuccess {
                    _state.update { oldState ->
                        oldState.copy(isLoading = false)
                    }
                }
                .onError { pocketDataError ->
                    ErrorDialogController.sendEvent(pocketDataError)
                    _state.update { oldState ->
                        oldState.copy(isLoading = false)
                    }
                }
        }
    }

}