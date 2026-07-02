package com.iti.pocketshop.features.search.presentation.view.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.search.domain.model.SortOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SortPanel(
    activeSortOption: SortOption,
    onOptionSelected: (SortOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Sort by",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SortOption.entries.forEach { option ->
                FilterOptionChip(
                    label = option.label,
                    isSelected = option == activeSortOption,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}
