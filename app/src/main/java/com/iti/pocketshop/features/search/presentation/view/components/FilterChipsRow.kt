package com.iti.pocketshop.features.search.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.search.domain.model.ProductFilterGroup
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.SortOption
import com.iti.pocketshop.features.search.domain.model.containsSelection
import com.iti.pocketshop.features.search.presentation.action.SearchAction

@Composable
fun FilterChipsRow(
    modifier: Modifier = Modifier,
    filterGroups: List<ProductFilterGroup>,
    activeFilters: List<ProductFilterValue>,
    activeSortOption: SortOption,
    activePriceRange: ClosedFloatingPointRange<Float>?,
    priceRangeBounds: ClosedFloatingPointRange<Float>?,
    onAction: (SearchAction) -> Unit,
    onOpenFiltersScreen: (() -> Unit)? = null,
) {
    if (filterGroups.isEmpty()) return

    fun activeCountInGroup(group: ProductFilterGroup): Int = when {
        group.type == "PRICE_RANGE" -> if (activePriceRange != null) 1 else 0
        else -> group.values.count { activeFilters.containsSelection(it) }
    }

    val activeGroups = filterGroups.filter { activeCountInGroup(it) > 0 }

    val totalActiveCount = activeFilters.size +
            (if (activeSortOption != SortOption.RELEVANCE) 1 else 0) +
            (if (activePriceRange != null) 1 else 0)

    var quickEditGroup by remember { mutableStateOf<ProductFilterGroup?>(null) }
    var showPriceSheet by remember { mutableStateOf(false) }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 8.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onOpenFiltersScreen != null) {
            item {
                FilterEntryButton(
                    activeCount = totalActiveCount,
                    onClick = onOpenFiltersScreen,
                )
            }
        }

        items(activeGroups, key = { it.id }) { group ->
            val activeCount = activeCountInGroup(group)
            val chipLabel = "${group.label} ($activeCount)"

            FilterChip(
                selected = true,
                onClick = {
                    if (group.type == "PRICE_RANGE") {
                        if (priceRangeBounds != null) {
                            showPriceSheet = true
                        } else {
                            onOpenFiltersScreen?.invoke()
                        }
                    } else {
                        quickEditGroup = group
                    }
                },
                label = {
                    Text(chipLabel, style = MaterialTheme.typography.labelMedium)
                },
                shape = RoundedCornerShape(6.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = true,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = MaterialTheme.colorScheme.secondary,
                ),
            )
        }
    }

    quickEditGroup?.let { group ->
        val latestGroup = filterGroups.firstOrNull { it.id == group.id } ?: group
        FilterPanelSheet(
            group = latestGroup,
            activeFilters = activeFilters,
            onAction = onAction,
            onDismiss = { quickEditGroup = null },
        )
    }

    if (showPriceSheet && priceRangeBounds != null) {
        PriceRangeSheet(
            bounds = priceRangeBounds,
            activeRange = activePriceRange,
            onAction = onAction,
            onDismiss = { showPriceSheet = false },
        )
    }
}
