package com.iti.pocketshop.features.cart.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iti.pocketshop.R

@Composable
fun RemoveCartItemDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Remove Item")
        },
        text = {
            Text(text = "Are you sure you want to remove this item from your cart?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Remove")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}
