package com.iti.pocketshop.features.search.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.LocalUser
import com.iti.pocketshop.R
import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.core.components.DeleteFavoriteDialogController
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.features.home.domain.models.toFavoriteProduct
import com.iti.pocketshop.features.productlist.presentation.ProductListAction
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.features.search.domain.model.toFavoriteProduct
import com.iti.pocketshop.features.search.presentation.action.SearchAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SearchResultsContent(
    query: String,
    searchResult: SearchResult,
    favoriteIds: Set<String>,
    isLoadingNextPage: Boolean,
    onAction: (SearchAction) -> Unit,
    modifier: Modifier = Modifier,
    user: UserSession? = LocalUser.current,
    scope: CoroutineScope = rememberCoroutineScope(),
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(
                    R.string.search_results_count,
                    searchResult.totalCount,
                    query
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        items(searchResult.products, key = { it.id }) { product ->
            val isFavorite = favoriteIds.contains(product.id)
            SearchProductGridCard(
                title = product.title,
                imageUrl = product.imageUrl,
                imageAlt = product.imageAlt,
                price = product.price,
                currencyCode = product.currencyCode,
                isFavorite = isFavorite,
                onWishlistClick = {
                    if (user?.isAnonymous == true) {
                        scope.launch {
                            SignInDialogController.sendEvent(true)
                        }
                    } else if (isFavorite) {
                        scope.launch {
                            DeleteFavoriteDialogController.sendEvent(product.toFavoriteProduct())
                        }
                    } else {
                        onAction(SearchAction.ToggleFavorite(product.toFavoriteProduct()))
                    }
                },
                onClick = { onAction(SearchAction.ClickProduct(product.id)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (searchResult.hasNextPage) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                TextButton(
                    onClick = { onAction(SearchAction.LoadNextPage) },
                    enabled = !isLoadingNextPage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                ) {
                    if (isLoadingNextPage) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text(stringResource(R.string.search_load_more))
                    }
                }
            }
        }
    }
}