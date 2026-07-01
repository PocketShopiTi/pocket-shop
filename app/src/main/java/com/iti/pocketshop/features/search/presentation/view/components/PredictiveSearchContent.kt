package com.iti.pocketshop.features.search.presentation.view.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import com.iti.pocketshop.features.search.presentation.action.SearchAction

@Composable
fun PredictiveSearchContent(
    predictiveResult: PredictiveSearchResult,
    onAction: (SearchAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
         items(predictiveResult.queries, key = { "query_${it.text}" }) { suggestion ->
            QuerySuggestionItem(
                suggestion = suggestion,
                onFillQuery = { onAction(SearchAction.UpdateQuery(it)) },
                onClick = {
                    onAction(SearchAction.UpdateQuery(suggestion.text))
                    onAction(SearchAction.SubmitSearch)
                },
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )
        }

         items(predictiveResult.products, key = { "product_${it.id}" }) { product ->
            ProductSuggestionItem(
                product = product,
                onFillQuery = { onAction(SearchAction.UpdateQuery(product.title)) },
                onClick = { onAction(SearchAction.ClickProduct(product.id)) },
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )
        }
    }
}


