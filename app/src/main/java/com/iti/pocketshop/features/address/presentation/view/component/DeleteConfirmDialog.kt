package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressState

@Composable
internal fun DeleteConfirmDialog(
    state: AddressState,
    onAction: (AddressAction) -> Unit,
) {
    val pendingDeleteAddressId = state.pendingDeleteAddressId ?: return
    val pendingAddress = state.addresses.firstOrNull { it.id == pendingDeleteAddressId }

    AlertDialog(
        onDismissRequest = { onAction(AddressAction.CancelDelete) },
        confirmButton = {
            Button(
                onClick = { onAction(AddressAction.ConfirmDelete) },
                enabled = !state.isSaving,
            ) {
                Text(stringResource(R.string.address_delete_confirm))
            }
        },
        dismissButton = {
            FilledTonalButton(onClick = { onAction(AddressAction.CancelDelete) }) {
                Text(stringResource(R.string.address_delete_cancel))
            }
        },
        icon = {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
        },
        title = { Text(stringResource(R.string.address_delete_title)) },
        text = {
            Text(
                text = pendingAddress?.let {
                    stringResource(R.string.address_delete_confirm_with_name, it.recipientName)
                } ?: stringResource(R.string.address_delete_confirm_generic),
            )
        },
    )
}
