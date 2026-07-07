package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
 import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

        shape = MaterialTheme.shapes.extraLarge,

        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },

        title = {
            Text(
                text = stringResource(R.string.address_delete_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                Text(
                    text = pendingAddress?.let {
                        stringResource(
                            R.string.address_delete_confirm_with_name,
                            it.recipientName
                        )
                    } ?: stringResource(R.string.address_delete_confirm_generic),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "This action cannot be undone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

        confirmButton = {
            Button(
                onClick = { onAction(AddressAction.ConfirmDelete) },
                enabled = !state.isSaving,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.address_delete_confirm))
            }
        },

        dismissButton = {
            TextButton(
                onClick = { onAction(AddressAction.CancelDelete) }
            ) {
                Text(stringResource(R.string.address_delete_cancel))
            }
        }
    )
}