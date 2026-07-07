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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.features.search.presentation.action.SearchAction

@Composable
fun SearchResultsContent(
    query: String,
    searchResult: SearchResult,
    isLoadingNextPage: Boolean,
    onAction: (SearchAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val products = searchResult.products

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

        items(products, key = { it.id }) { product ->
            SearchProductGridCard(
                title = product.title,
                imageUrl = product.imageUrl,
                imageAlt = product.imageAlt,
                price = product.price,
                currencyCode = product.currencyCode,
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