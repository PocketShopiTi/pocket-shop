package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun SectionTitle(
    title: String,
    subtitle: String? = null,
) {
    val extendedColors = LocalExtendedColors.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .background(
                    color = extendedColors.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(999.dp),
                )
                .border(
                    width = 1.dp,
                    color = extendedColors.primary.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(999.dp),
                )
                .padding(horizontal = 14.dp, vertical = 7.dp),
        ) {
            Text(
                text = title,
                color = extendedColors.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
        }

        subtitle?.takeIf { it.isNotBlank() }?.let { subtitleText ->
            Text(
                text = subtitleText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
