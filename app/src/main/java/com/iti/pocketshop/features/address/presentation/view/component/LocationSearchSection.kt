package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun LocationSearchSection(
    editor: AddressEditorState,
    enabled: Boolean,
    onAction: (AddressAction) -> Unit,
) {
    val extendedColors = LocalExtendedColors.current
    val showDropdown = editor.isLocationSearching || editor.hasSearchResult
    val suggestionItems = editor.locationSuggestions

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(
            title = stringResource(R.string.address_section_location),

        )

        AddressFieldTextField(
            value = editor.locationSearchQuery,
            label = stringResource(R.string.address_location_search_label),
            error = null,
            enabled = enabled,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = extendedColors.textSecondary,
                )
            },
            trailingIcon = if (editor.isLocationSearching) {
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                }
            } else {
                null
            },
            onValueChange = { onAction(AddressAction.LocationSearchChanged(it)) },
        )

        if (showDropdown) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
                when {
                    editor.isLocationSearching -> {
                        SearchStatusRow(
                            text = stringResource(R.string.address_location_searching),
                            showProgress = true,
                        )
                    }

                    suggestionItems.isNotEmpty() -> {
                        suggestionItems.forEachIndexed { index, suggestion ->
                            LocationSuggestionRow(
                                suggestion = suggestion,
                                enabled = enabled,
                                onClick = {
                                    onAction(AddressAction.LocationSuggestionSelected(suggestion))
                                },
                            )
                            if (index < suggestionItems.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }

                    else -> {
                        SearchStatusRow(
                            text = stringResource(R.string.address_location_no_results),
                            showProgress = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchStatusRow(
    text: String,
    showProgress: Boolean,
) {
    val extendedColors = LocalExtendedColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
            )
        }
        Text(
            text = text,
            color = extendedColors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}