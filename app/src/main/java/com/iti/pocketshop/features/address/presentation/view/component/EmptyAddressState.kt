package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.iti.pocketshop.R
import com.iti.pocketshop.ui.theme.LocalExtendedColors
import java.util.Locale

@Composable
internal fun EmptyAddressState(
    customerName: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onAddAddress: () -> Unit,
) {
    val extendedColors = LocalExtendedColors.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.location))

    val formattedName = remember(customerName) {
        customerName.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(250.dp)
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = if (formattedName.isBlank()) {
                stringResource(R.string.address_empty_title)
            } else {
                stringResource(R.string.address_empty_title_with_name, formattedName)
            },
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = extendedColors.textPrimary,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.address_empty_subtitle),
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = extendedColors.textSecondary,
            textAlign = TextAlign.Center,
        )

        Button(
            onClick = onAddAddress,
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.primary,
                contentColor = extendedColors.onPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(52.dp),
        ) {
            Text(
                text = stringResource(R.string.address_add_new_address),
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}