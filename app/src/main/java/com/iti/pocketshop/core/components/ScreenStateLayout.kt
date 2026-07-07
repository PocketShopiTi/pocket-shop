package com.iti.pocketshop.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.toUserMessage

@Composable
fun ScreenStateLayout(
    isLoading: Boolean,
    error: PocketDataError?,
    isEmpty: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable BoxScope.() -> Unit,
    emptyContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> loadingContent()
            error != null -> ScreenErrorContent(
                message = error.toUserMessage(LocalContext.current),
                onRetry = onRetry,
            )
            isEmpty -> emptyContent()
            else -> content()
        }

        OfflineBanner(
            isVisible = error == PocketDataError.Remote.NO_INTERNET,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Composable
private fun ScreenErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}
