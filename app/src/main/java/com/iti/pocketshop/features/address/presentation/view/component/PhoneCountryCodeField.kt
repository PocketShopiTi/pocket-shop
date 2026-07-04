package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.address.presentation.state.PhoneCountryCode
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PhoneCountryCodeField(
    selectedCountryCode: PhoneCountryCode,
    enabled: Boolean,
    onCountrySelected: (PhoneCountryCode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val extendedColors = LocalExtendedColors.current
    val context = LocalContext.current
    val countryCodes = remember(context) {
        PhoneCountryCode.entries.sortedWith(
            compareBy<PhoneCountryCode> { it == PhoneCountryCode.INTERNATIONAL }
                .thenBy { it.label(context) },
        )
    }
    val selectedCountryDisplayLabel = selectedCountryCode.displayLabel(context)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selectedCountryDisplayLabel,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = enabled,
                ),
            enabled = enabled,
            readOnly = true,
            singleLine = true,
            leadingIcon = {
                Text(
                    text = selectedCountryCode.flagEmoji(context),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = extendedColors.primary,
                unfocusedBorderColor = extendedColors.outline,
                focusedContainerColor = extendedColors.surface,
                unfocusedContainerColor = extendedColors.surface,
                cursorColor = extendedColors.primary,
                focusedTextColor = extendedColors.textPrimary,
                unfocusedTextColor = extendedColors.textPrimary,
            ),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp),
        ) {
            countryCodes.forEach { option ->
                val optionLabel = option.displayLabel(context)
                DropdownMenuItem(
                    enabled = enabled,
                    text = {
                        Text(
                            text = optionLabel,
                            fontWeight = if (option == selectedCountryCode) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                        )
                    },
                    leadingIcon = {
                        Text(text = option.flagEmoji(context))
                    },
                    trailingIcon = if (option == selectedCountryCode) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                            )
                        }
                    } else {
                        null
                    },
                    onClick = {
                        expanded = false
                        onCountrySelected(option)
                    },
                )
            }
        }
    }
}
