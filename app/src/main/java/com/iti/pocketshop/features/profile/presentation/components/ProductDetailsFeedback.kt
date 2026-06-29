package com.iti.pocketshop.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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

@Composable
internal fun LoadingContent(modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.product_details_loading)
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = loadingDescription },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
internal fun ErrorContent(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.profile_error),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(R.string.product_details_retry))
        }
    }
}
