package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun LocationSuggestionRow(
    suggestion: AddressLocationSuggestion,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val extendedColors = LocalExtendedColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = extendedColors.textSecondary,
            modifier = Modifier.size(18.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = suggestion.primaryText.ifBlank { suggestion.placeId },
                fontWeight = FontWeight.Medium,
                color = extendedColors.textPrimary,
                fontSize = 15.sp,
            )
            if (suggestion.secondaryText.isNotBlank()) {
                Text(
                    text = suggestion.secondaryText,
                    color = extendedColors.textSecondary,
                    fontSize = 13.sp,
                )
            }
        }
    }
}
