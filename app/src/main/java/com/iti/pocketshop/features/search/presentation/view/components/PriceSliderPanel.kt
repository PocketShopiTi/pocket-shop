package com.iti.pocketshop.features.search.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.LocalSettingsUser
import com.iti.pocketshop.R
import com.iti.pocketshop.core.pricing.PriceFormatter

@Composable
fun PriceSliderPanel(
    bounds: ClosedFloatingPointRange<Float>,
    activeRange: ClosedFloatingPointRange<Float>?,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onClear: () -> Unit,
) {
    val userSettings = LocalSettingsUser.current
    val currentRange = activeRange ?: bounds

    var sliderPosition by remember(currentRange) { mutableStateOf(currentRange) }
    val formattedStart = PriceFormatter.format(
        amount = sliderPosition.start.toDouble(),
        sourceCurrencyCode = "USD",
        userSettings = userSettings,
    )
    val formattedEnd = PriceFormatter.format(
        amount = sliderPosition.endInclusive.toDouble(),
        sourceCurrencyCode = "USD",
        userSettings = userSettings,
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.search_price_range),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            if (activeRange != null) {
                TextButton(onClick = onClear) {
                    Text(stringResource(R.string.search_filters_clear))
                }
            }
        }

        Text(
            text = "$formattedStart - $formattedEnd",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        RangeSlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = bounds,
            onValueChangeFinished = {
                onRangeChanged(sliderPosition)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
