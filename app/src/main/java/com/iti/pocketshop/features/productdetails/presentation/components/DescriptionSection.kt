package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R

private const val COLLAPSED_DESCRIPTION_LINES = 2

@Composable
internal fun DescriptionSection(
    description: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    var canExpand by remember(description) { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = stringResource(R.string.product_details_description)
                .uppercase(locale),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = sectionLabelStyle(),
        )

        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 21.sp
            ),
            maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_DESCRIPTION_LINES,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                if (!expanded) canExpand = result.hasVisualOverflow
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .animateContentSize(),
        )

        if (canExpand) {
            NoRippleTextButton(
                text = stringResource(
                    if (expanded) R.string.product_details_read_less
                    else R.string.product_details_read_more,
                ),
                onClick = onToggle,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
