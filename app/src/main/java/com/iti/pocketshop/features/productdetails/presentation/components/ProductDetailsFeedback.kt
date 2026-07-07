package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.core.components.ShimmerPlaceholder

@Composable
internal fun LoadingContent(modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.product_details_loading)
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = loadingDescription },
    ) {
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.92f),
            shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ShimmerPlaceholder(Modifier.width(96.dp).height(12.dp))
            ShimmerPlaceholder(Modifier.fillMaxWidth(0.82f).height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ShimmerPlaceholder(Modifier.width(112.dp).height(18.dp))
                ShimmerPlaceholder(Modifier.width(88.dp).height(24.dp))
            }
            ShimmerPlaceholder(Modifier.fillMaxWidth().height(1.dp))
            ShimmerPlaceholder(Modifier.width(150.dp).height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(4) {
                    ShimmerPlaceholder(Modifier.width(56.dp).height(42.dp))
                }
            }
        }
    }
}

@Composable
internal fun EmptyProductContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.product_details_empty),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(stringResource(R.string.product_details_retry))
        }
    }
}
