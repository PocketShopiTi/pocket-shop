package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun ColourSwatch(
    colour: Color?,
    imageUrl: String?,
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val unavailable = stringResource(R.string.product_details_option_unavailable, label)
    Box(
        modifier = Modifier
            .size(36.dp)
            .semantics { if (!enabled) contentDescription = unavailable }
            .clip(CircleShape)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape,
            )
            .padding(3.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = enabled, onClick = onClick),
    ) {
        when {
            imageUrl != null -> AsyncImage(
                model = imageUrl,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
            )
            colour != null -> Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(colour.copy(alpha = if (enabled) 1f else 0.35f)),
            )
        }
    }
}
