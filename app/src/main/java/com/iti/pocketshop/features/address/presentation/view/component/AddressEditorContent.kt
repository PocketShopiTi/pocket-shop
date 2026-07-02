package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressField
import com.iti.pocketshop.features.address.presentation.state.AddressState
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun AddressEditorContent(
    state: AddressState,
    onAction: (AddressAction) -> Unit,
) {
    val editor = state.editor
    val scrollState = rememberScrollState()
    val isBusy = state.isLoading || state.isSaving
    val locationBusy = editor.isLocationSearching || editor.isLocationResolving
    val hasMapsKey = BuildConfig.MAPS_API_KEY.isNotBlank()
    val extendedColors = LocalExtendedColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        SectionTitle(
            title = stringResource(R.string.address_section_recipient),
        )

        DoubleFieldRow(
            first = {
                AddressFieldTextField(
                    value = editor.firstName,
                    label = stringResource(R.string.address_label_first_name),
                    error = editor.validationErrors[AddressField.FIRST_NAME],
                    enabled = !isBusy,
                    keyboardType = KeyboardType.Text,
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = extendedColors.textSecondary,
                        )
                    },
                    onValueChange = { onAction(AddressAction.FieldChanged(AddressField.FIRST_NAME, it)) },
                )
            },
            second = {
                AddressFieldTextField(
                    value = editor.lastName,
                    label = stringResource(R.string.address_label_last_name),
                    error = editor.validationErrors[AddressField.LAST_NAME],
                    enabled = !isBusy,
                    keyboardType = KeyboardType.Text,
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = extendedColors.textSecondary,
                        )
                    },
                    onValueChange = { onAction(AddressAction.FieldChanged(AddressField.LAST_NAME, it)) },
                )
            },
        )

        AddressFieldTextField(
            value = editor.phone,
            label = stringResource(R.string.address_label_phone_number),
            error = editor.validationErrors[AddressField.PHONE],
            enabled = !isBusy,
            keyboardType = KeyboardType.Phone,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = extendedColors.textSecondary,
                )
            },
            onValueChange = { onAction(AddressAction.FieldChanged(AddressField.PHONE, it)) },
        )

        AddressFieldTextField(
            value = editor.country,
            label = stringResource(R.string.address_label_country),
            error = editor.validationErrors[AddressField.COUNTRY],
            enabled = !isBusy,
            keyboardType = KeyboardType.Text,
            leadingIcon = {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = extendedColors.textSecondary,
                )
            },
            onValueChange = { onAction(AddressAction.FieldChanged(AddressField.COUNTRY, it)) },
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionTitle(
                title = stringResource(R.string.address_section_address_details),
            )

            AddressFieldTextField(
                value = editor.address1,
                label = stringResource(R.string.address_label_street_address),
                error = editor.validationErrors[AddressField.ADDRESS1],
                enabled = !isBusy,
                keyboardType = KeyboardType.Text,
                leadingIcon = {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = null,
                        tint = extendedColors.textSecondary,
                    )
                },
                onValueChange = { onAction(AddressAction.FieldChanged(AddressField.ADDRESS1, it)) },
            )

            AddressFieldTextField(
                value = editor.address2,
                label = stringResource(R.string.address_label_apartment_suite),
                error = null,
                enabled = !isBusy,
                keyboardType = KeyboardType.Text,
                leadingIcon = {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = null,
                        tint = extendedColors.textSecondary,
                    )
                },
                onValueChange = { onAction(AddressAction.FieldChanged(AddressField.ADDRESS2, it)) },
            )

            DoubleFieldRow(
                first = {
                    AddressFieldTextField(
                        value = editor.city,
                        label = stringResource(R.string.address_label_city),
                        error = editor.validationErrors[AddressField.CITY],
                        enabled = !isBusy,
                        keyboardType = KeyboardType.Text,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = extendedColors.textSecondary,
                            )
                        },
                        onValueChange = { onAction(AddressAction.FieldChanged(AddressField.CITY, it)) },
                    )
                },
                second = {
                    AddressFieldTextField(
                        value = editor.province,
                        label = stringResource(R.string.address_label_state_province),
                        error = null,
                        enabled = !isBusy,
                        keyboardType = KeyboardType.Text,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = extendedColors.textSecondary,
                            )
                        },
                        onValueChange = { onAction(AddressAction.FieldChanged(AddressField.PROVINCE, it)) },
                    )
                },
            )

            DoubleFieldRow(
                first = {
                    AddressFieldTextField(
                        value = editor.zip,
                        label = stringResource(R.string.address_label_postal_code),
                        error = editor.validationErrors[AddressField.ZIP],
                        enabled = !isBusy,
                        keyboardType = KeyboardType.Text,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = null,
                                tint = extendedColors.textSecondary,
                            )
                        },
                        onValueChange = { onAction(AddressAction.FieldChanged(AddressField.ZIP, it)) },
                    )
                },
                second = {
                    AddressFieldTextField(
                        value = editor.company,
                        label = stringResource(R.string.address_label_company),
                        error = null,
                        enabled = !isBusy,
                        keyboardType = KeyboardType.Text,
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = extendedColors.textSecondary,
                            )
                        },
                        onValueChange = { onAction(AddressAction.FieldChanged(AddressField.COMPANY, it)) },
                    )
                },
            )
        }

        MapPreviewCard(
            latitude = editor.latitude,
            longitude = editor.longitude,
            isLoading = editor.isLocationResolving,
            enabled = !isBusy,
            hasMapsKey = hasMapsKey,
            onLocationPicked = { latitude, longitude ->
                onAction(AddressAction.MapLocationPicked(latitude, longitude))
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.address_set_default_title),
                    color = extendedColors.textPrimary,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = stringResource(R.string.address_set_default_subtitle),
                    color = extendedColors.textSecondary,
                    fontSize = 13.sp,
                )
            }
            Switch(
                checked = editor.isDefault,
                enabled = !isBusy,
                onCheckedChange = { onAction(AddressAction.ToggleDefault) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = extendedColors.surface,
                    checkedTrackColor = extendedColors.primary,
                    uncheckedThumbColor = extendedColors.surface,
                    uncheckedTrackColor = extendedColors.outline,
                ),
            )
        }

        BottomPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = if (state.isSaving) {
                stringResource(R.string.address_saving)
            } else {
                stringResource(R.string.address_save)
            },
            onClick = { onAction(AddressAction.SaveClicked) },
            enabled = !isBusy && !locationBusy,
            showProgress = state.isSaving,
        )
    }
}
