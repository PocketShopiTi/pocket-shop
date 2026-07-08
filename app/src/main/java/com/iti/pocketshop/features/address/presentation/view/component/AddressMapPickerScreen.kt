package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapUiSettings
import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun AddressMapPickerScreen(
    editor: AddressEditorState,
    enabled: Boolean,
    onAction: (AddressAction) -> Unit,
    onClose: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val defaultLatitude = stringResource(R.string.address_map_default_latitude).toDouble()
    val defaultLongitude = stringResource(R.string.address_map_default_longitude).toDouble()
    val defaultZoom = stringResource(R.string.address_map_default_zoom).toFloat()

    var displayQuery by remember(editor.addressId) {
        mutableStateOf(editor.locationSearchQuery)
    }
    var pendingLatitude by remember(editor.addressId) {
        mutableStateOf(editor.latitude)
    }
    var pendingLongitude by remember(editor.addressId) {
        mutableStateOf(editor.longitude)
    }
    var selectionLabel by remember(editor.addressId) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(editor.latitude, editor.longitude) {
        if (pendingLatitude == null && pendingLongitude == null) {
            pendingLatitude = editor.latitude
            pendingLongitude = editor.longitude
        }
    }

    val latitude = pendingLatitude
    val longitude = pendingLongitude
    val hasSelection = latitude != null && longitude != null
    val selectedLatLng = latitude?.let { lat ->
        longitude?.let { lng ->
            LatLng(lat, lng)
        }
    }
    val cameraTarget = selectedLatLng ?: LatLng(defaultLatitude, defaultLongitude)

    Box(modifier = modifier.fillMaxSize()) {
        AddressGoogleMapView(
            target = cameraTarget,
            zoom = defaultZoom,
            marker = selectedLatLng,
            enabled = enabled,
            onLocationPicked = { latitude, longitude ->
                pendingLatitude = latitude
                pendingLongitude = longitude
                selectionLabel = null
            },
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(
                compassEnabled = true,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
            ),
        )

        AddressMapTopBar(
            query = displayQuery,
            enabled = enabled,
            onQueryChange = { query ->
                displayQuery = query
                onAction(AddressAction.LocationSearchChanged(query))
            },
            onBackClick = onClose,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        AddressMapSearchResults(
            query = displayQuery,
            editor = editor,
            enabled = enabled,
            onSuggestionPicked = { suggestion ->
                suggestion.toLatLngOrNull()?.let { latLng ->
                    pendingLatitude = latLng.latitude
                    pendingLongitude = latLng.longitude
                    selectionLabel = suggestion.primaryText
                        .ifBlank { suggestion.secondaryText }
                        .ifBlank { displayQuery }
                    displayQuery = suggestion.primaryText
                        .ifBlank { suggestion.secondaryText }
                        .ifBlank { displayQuery }
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 128.dp)
                .statusBarsPadding(),
        )

        AddressMapBottomBar(
            hasSelection = hasSelection,
            latitude = pendingLatitude,
            longitude = pendingLongitude,
            selectionLabel = selectionLabel,
            isResolving = editor.isLocationResolving,
            enabled = enabled && hasSelection && !editor.isLocationResolving,
            onDone = {
                val selectedLatitude = pendingLatitude
                val selectedLongitude = pendingLongitude
                if (selectedLatitude != null && selectedLongitude != null) {
                    onAction(AddressAction.MapLocationPicked(selectedLatitude, selectedLongitude))
                    onDone()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (editor.isLocationResolving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun AddressMapTopBar(
    query: String,
    enabled: Boolean,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extendedColors = LocalExtendedColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(42.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.address_content_description_back),
                    tint = extendedColors.primary,
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = extendedColors.textSecondary,
                    modifier = Modifier.size(18.dp),
                )

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = extendedColors.textPrimary,
                        fontSize = 14.sp,
                    ),
                    cursorBrush = SolidColor(extendedColors.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.address_location_search_hint),
                                color = extendedColors.textSecondary,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        inner()
                    },
                )

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(18.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear_search),
                            tint = extendedColors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressMapSearchResults(
    query: String,
    editor: AddressEditorState,
    enabled: Boolean,
    onSuggestionPicked: (AddressLocationSuggestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasSearchQuery = query.trim().length >= 3
    val showResults = editor.isLocationSearching || (hasSearchQuery && editor.hasSearchResult)
    val suggestions = editor.locationSuggestions

    if (!showResults) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        when {
            editor.isLocationSearching -> SearchStatusRow(
                text = stringResource(R.string.address_location_searching),
                showProgress = true,
            )

            suggestions.isNotEmpty() -> {
                suggestions.forEachIndexed { index, suggestion ->
                    LocationSuggestionRow(
                        suggestion = suggestion,
                        enabled = enabled,
                        onClick = { onSuggestionPicked(suggestion) },
                    )
                    if (index < suggestions.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }

            else -> SearchStatusRow(
                text = stringResource(R.string.address_location_no_results),
                showProgress = false,
            )
        }
    }
}

@Composable
private fun SearchStatusRow(
    text: String,
    showProgress: Boolean,
) {
    val extendedColors = LocalExtendedColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (showProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = extendedColors.textSecondary,
                modifier = Modifier.size(18.dp),
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

@Composable
private fun AddressMapBottomBar(
    hasSelection: Boolean,
    latitude: Double?,
    longitude: Double?,
    selectionLabel: String?,
    isResolving: Boolean,
    enabled: Boolean,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extendedColors = LocalExtendedColors.current
    val summary = selectionLabel?.takeIf { it.isNotBlank() }
        ?: if (hasSelection && latitude != null && longitude != null) {
            stringResource(R.string.address_map_coordinates, latitude, longitude)
        } else {
            stringResource(R.string.address_map_picker_no_location)
        }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.address_map_pick_hint),
                color = extendedColors.textPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = summary,
                color = extendedColors.textSecondary,
                fontSize = 13.sp,
            )
            BottomPrimaryButton(
                text = stringResource(R.string.address_map_use_location),
                onClick = onDone,
                enabled = enabled,
                showProgress = isResolving,
            )
        }
    }
}

private fun AddressLocationSuggestion.toLatLngOrNull(): LatLng? {
    val raw = placeId.removePrefix("geo:")
    val parts = raw.split(",")
    if (parts.size < 2) {
        return null
    }

    val latitude = parts[0].toDoubleOrNull() ?: return null
    val longitude = parts[1].toDoubleOrNull() ?: return null
    return LatLng(latitude, longitude)
}
