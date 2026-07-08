package com.iti.pocketshop.features.search.presentation.view
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.search.domain.model.ProductFilterGroup
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.SortOption
import com.iti.pocketshop.features.search.domain.model.containsSelection
import com.iti.pocketshop.features.search.presentation.action.SearchAction
import com.iti.pocketshop.features.search.presentation.view.components.FilterPanel
import com.iti.pocketshop.features.search.presentation.view.components.PriceSliderPanel
import com.iti.pocketshop.features.search.presentation.view.components.SortPanel

private const val SORT_KEY = "__sort__"

sealed class SidebarItem {
    object Sort : SidebarItem()
    data class FilterGroup(val group: ProductFilterGroup) : SidebarItem()
}

 private val SidebarItem.stableKey: String
    get() = when (this) {
        is SidebarItem.Sort -> SORT_KEY
        is SidebarItem.FilterGroup -> group.id
    }

@Composable
fun FiltersScreen(
    filterGroups: List<ProductFilterGroup>,
    activeFilters: List<ProductFilterValue>,
    activeSortOption: SortOption,
    priceRangeBounds: ClosedFloatingPointRange<Float>?,
    activePriceRange: ClosedFloatingPointRange<Float>?,
    onAction: (SearchAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedGroupId: String? = null,
) {
     val sidebarItems: List<SidebarItem> = buildList {
        add(SidebarItem.Sort)
        addAll(filterGroups.map { SidebarItem.FilterGroup(it) })
    }

       var selectedKey by rememberSaveable(filterGroups.map { it.id }) {
        mutableStateOf(initialSelectedGroupId ?: SORT_KEY)
    }

    val selectedSidebarItem = sidebarItems.firstOrNull { it.stableKey == selectedKey }
        ?: sidebarItems.firstOrNull()

     val activeCount = activeFilters.size +
            (if (activeSortOption != SortOption.RELEVANCE) 1 else 0) +
            (if (activePriceRange != null) 1 else 0)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_filters_title, activeCount)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { onAction(SearchAction.ClearFilters) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.search_filters_clear_all))
                }
                Button(
                    onClick = {
                        onAction(SearchAction.SubmitSearch)
                        onBack()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.search_filters_show_results))
                }
            }
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
             LazyColumn(
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                items(sidebarItems, key = { it.stableKey }) { item ->
                    val isSelected = item.stableKey == selectedSidebarItem?.stableKey
                    val label = when (item) {
                        is SidebarItem.Sort -> stringResource(R.string.search_sort_by)
                        is SidebarItem.FilterGroup -> item.group.label
                    }
                    val activeCountInItem = when (item) {
                        is SidebarItem.Sort -> if (activeSortOption != SortOption.RELEVANCE) 1 else 0
                        is SidebarItem.FilterGroup -> {
                            if (item.group.type == "PRICE_RANGE" && activePriceRange != null) {
                                1
                            } else {
                                item.group.values.count { activeFilters.containsSelection(it) }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                            )
                            .clickable { selectedKey = item.stableKey }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f),
                        )
                        if (activeCountInItem > 0) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$activeCountInItem",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }

             Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                when (selectedSidebarItem) {
                    is SidebarItem.Sort -> {
                        SortPanel(
                            activeSortOption = activeSortOption,
                            onOptionSelected = { onAction(SearchAction.SelectSortOption(it)) }
                        )
                    }
                    is SidebarItem.FilterGroup -> {
                        if (selectedSidebarItem.group.type == "PRICE_RANGE" && priceRangeBounds != null) {
                            PriceSliderPanel(
                                bounds = priceRangeBounds,
                                activeRange = activePriceRange,
                                onRangeChanged = { onAction(SearchAction.UpdatePriceRange(it)) },
                                onClear = { onAction(SearchAction.ClearPriceRange) },
                            )
                        } else {
                            FilterPanel(
                                group = selectedSidebarItem.group,
                                activeFilters = activeFilters,
                                onAction = onAction
                            )
                        }
                    }
                    null -> {
                        Text(
                            text = stringResource(R.string.search_filters_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

