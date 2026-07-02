package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressState

@Composable
internal fun AddressListContent(
    state: AddressState,
    onAction: (AddressAction) -> Unit,
) {
    if (state.isLoading) {
        LoadingPanel()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.addresses.isEmpty()) {
            EmptyAddressState(
                customerName = state.customerName,
                modifier = Modifier.align(Alignment.Center),
                enabled = !state.isSaving,
                onAddAddress = { onAction(AddressAction.AddAddressClicked) },
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 10.dp,
                    end = 20.dp,
                    bottom = 110.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    SummaryCard(
                        customerName = state.customerName,
                        addressCount = state.addresses.size,
                        defaultAddressId = state.defaultAddressId,
                    )
                }

                items(
                    items = state.addresses,
                    key = { address -> address.id },
                ) { address ->
                    AddressCard(
                        address = address,
                        enabled = !state.isLoading && !state.isSaving,
                        onEdit = { onAction(AddressAction.EditAddressClicked(address.id)) },
                        onDelete = { onAction(AddressAction.DeleteClicked(address.id)) },
                        onMakeDefault = { onAction(AddressAction.SetDefaultClicked(address.id)) },
                    )
                }
            }
        }

        BottomAddAddressButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            enabled = !state.isSaving,
            onClick = { onAction(AddressAction.AddAddressClicked) },
        )
    }
}
