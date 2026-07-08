package com.iti.pocketshop.features.search.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.features.search.presentation.action.SearchAction

@Composable
fun SearchInitialContent(
    products: List<SearchResultItem.ProductItem>,
    onAction: (SearchAction) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
) {
    val suggestedKeywords = stringArrayResource(R.array.suggested_keywords)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.trending_searches),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    suggestedKeywords.forEach { keyword ->
                        Surface(
                            onClick = {
                                onAction(SearchAction.UpdateQuery(keyword))
                                onAction(SearchAction.SubmitSearch)
                                focusManager.clearFocus()
                            },
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        ) {
                            Text(
                                text = keyword,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        if (products.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.recommended_for_you),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            items(products, key = { it.id }) { product ->
                SearchProductCard(
                    title = product.title,
                    imageUrl = product.imageUrl,
                    imageAlt = product.imageAlt,
                    price = product.price,
                    currencyCode = product.currencyCode,
                    onClick = { onAction(SearchAction.ClickProduct(product.id)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}