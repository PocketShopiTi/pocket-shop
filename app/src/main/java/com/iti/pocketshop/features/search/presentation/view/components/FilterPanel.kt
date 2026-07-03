package com.iti.pocketshop.features.search.presentation.view.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.search.domain.model.ProductFilterGroup
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.containsSelection
import com.iti.pocketshop.features.search.presentation.action.SearchAction


@OptIn(ExperimentalLayoutApi::class)
@Composable
  fun FilterPanel(
    group: ProductFilterGroup,
    activeFilters: List<ProductFilterValue>,
    onAction: (SearchAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = group.label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            group.values.forEach { value ->
                val isSelected = activeFilters.containsSelection(value)
                val isEnabled = isSelected || value.count > 0
                FilterOptionChip(
                    label = value.labelWithCount(),
                    isSelected = isSelected,
                    enabled = isEnabled,
                    onClick = { onAction(SearchAction.ToggleFilter(value)) }
                )
            }
        }
    }
}
