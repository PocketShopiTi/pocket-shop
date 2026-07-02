package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun EmptyAddressState(
    customerName: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onAddAddress: () -> Unit,
) {
    val extendedColors = LocalExtendedColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(extendedColors.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = extendedColors.textSecondary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (customerName.isBlank()) {
                stringResource(R.string.address_empty_title)
            } else {
                stringResource(R.string.address_empty_title_with_name, customerName)
            },
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = extendedColors.textPrimary,
        )

        Text(
            text = stringResource(R.string.address_empty_subtitle),
            color = extendedColors.textSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddAddress,
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.primary,
                contentColor = extendedColors.onPrimary,
            ),
            modifier = Modifier.padding(horizontal = 32.dp).height(48.dp),
        ) {
            Text(
                text = stringResource(R.string.address_add_new_address),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
