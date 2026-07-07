package com.iti.pocketshop.features.profile.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.core.components.ShimmerPlaceholder

@Composable
fun ProfileLoadingContent(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.profile_loading)
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = description }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ShimmerPlaceholder(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerPlaceholder(Modifier.width(96.dp).height(11.dp))
                ShimmerPlaceholder(Modifier.fillMaxWidth(0.72f).height(24.dp))
                ShimmerPlaceholder(Modifier.width(132.dp).height(11.dp))
            }
        }

        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp),
            shape = RoundedCornerShape(16.dp),
        )

        ShimmerPlaceholder(Modifier.width(156.dp).height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(112.dp),
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }

        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(224.dp),
            shape = RoundedCornerShape(16.dp),
        )
    }
}

@Composable
fun ProfileErrorCard(
    message: String,
    onReload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.error,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onReload,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(R.string.profile_reload))
            }
        }
    }
}
