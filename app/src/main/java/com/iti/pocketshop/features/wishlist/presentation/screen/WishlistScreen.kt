package com.iti.pocketshop.features.wishlist.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.features.wishlist.presentation.action.WishlistAction
import com.iti.pocketshop.features.wishlist.presentation.state.WishlistState
import com.iti.pocketshop.features.wishlist.presentation.viewmodel.WishlistViewModel

@Composable
fun WishlistRoot(
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WishlistScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun WishlistScreen(
    state: WishlistState,
    onAction: (WishlistAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        TopAppBar(
            title = { Text(text = "Wishlist") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            windowInsets = TopAppBarDefaults.windowInsets.exclude(WindowInsets.statusBars)
        )
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(WishlistAction.FetchFavorites) }
        ) {

        }
    }
}