package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import com.iti.pocketshop.R
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun BottomAddAddressButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val extendedColors = LocalExtendedColors.current

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.5.dp, extendedColors.primary.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = extendedColors.primary,
        ),
    ) {
        Text(stringResource(R.string.address_add_new_address), fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun AddressEditorBottomBar(
    enabled: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        BottomPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = if (isSaving) {
                stringResource(R.string.address_saving)
            } else {
                stringResource(R.string.address_save)
            },
            onClick = onSave,
            enabled = enabled,
            showProgress = isSaving,
        )
    }
}

@Composable
internal fun BottomPrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    showProgress: Boolean = false,
) {
    val extendedColors = LocalExtendedColors.current

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = extendedColors.primary,
            contentColor = extendedColors.onPrimary,
        ),
    ) {
        if (showProgress) {
            CircularProgressIndicator(
                modifier = Modifier.height(18.dp).width(18.dp),
                strokeWidth = 2.dp,
                color = extendedColors.onPrimary,
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(text, fontWeight = FontWeight.Medium)
    }
}
