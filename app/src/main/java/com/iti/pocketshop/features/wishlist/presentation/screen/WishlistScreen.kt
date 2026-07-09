package com.iti.pocketshop.features.wishlist.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.core.components.DeleteFavoriteDialogController
import com.iti.pocketshop.core.components.RemoveFavoriteDialog
import com.iti.pocketshop.features.wishlist.presentation.action.WishlistAction
import com.iti.pocketshop.features.wishlist.presentation.components.WishlistEmptyState
import com.iti.pocketshop.features.wishlist.presentation.components.WishlistGrid
import com.iti.pocketshop.features.wishlist.presentation.components.WishlistTopAppBar
import com.iti.pocketshop.features.wishlist.presentation.state.WishlistState
import com.iti.pocketshop.features.wishlist.presentation.viewmodel.WishlistViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WishlistRoot(
    openProductDetails: (String) -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WishlistScreen(
        openProductDetails = openProductDetails,
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistScreen(
    state: WishlistState,
    openProductDetails: (String) -> Unit,
    onAction: (WishlistAction) -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        WishlistTopAppBar()

        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = state.isLoading,
            onRefresh = { onAction(WishlistAction.FetchFavorites) },
            modifier = Modifier.fillMaxSize(),
            indicator = {
                LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = state.isLoading,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
        ) {
            if (!state.isLoading && state.favorites.isEmpty()) {
                WishlistEmptyState()
            }
            if (state.favorites.isNotEmpty()) {
                WishlistGrid(
                    favorites = state.favorites,
                    openProductDetails = openProductDetails,
                    onToggleFavorite = { favoriteProduct ->
                        scope.launch {
                            DeleteFavoriteDialogController.sendEvent(favoriteProduct)
                        }
                    }
                )
            }
        }
    }

    RemoveFavoriteDialog(
        onConfirm = {
            onAction(WishlistAction.ToggleFavorite(it))
        }
    )
}