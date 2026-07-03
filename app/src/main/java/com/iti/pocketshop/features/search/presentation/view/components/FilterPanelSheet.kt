package com.iti.pocketshop.features.search.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.search.domain.model.ProductFilterGroup
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.containsSelection
import com.iti.pocketshop.features.search.presentation.action.SearchAction


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanelSheet(
    group: ProductFilterGroup,
    activeFilters: List<ProductFilterValue>,
    onAction: (SearchAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = group.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(group.values, key = { it.id }) { value ->
                    val isChecked = activeFilters.containsSelection(value)
                    val isEnabled = isChecked || value.count > 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = isChecked,
                            enabled = isEnabled,
                            onCheckedChange = { onAction(SearchAction.ToggleFilter(value)) },
                        )
                        Text(
                            text = value.labelWithCount(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        group.values.filter { activeFilters.containsSelection(it) }
                            .forEach { onAction(SearchAction.ToggleFilter(it)) }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.search_filters_clear))
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.search_filters_show_results))
                }
            }
        }
    }
}

fun ProductFilterValue.labelWithCount(): String {
    return if (count > 0) "${label} (${count})" else label
}
