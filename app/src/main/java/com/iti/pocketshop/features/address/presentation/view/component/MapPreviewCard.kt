package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapUiSettings
import com.iti.pocketshop.R


@Composable
internal fun MapPreviewCard(
    latitude: Double?,
    longitude: Double?,
    isLoading: Boolean,
    enabled: Boolean,
    errorMessage: String?,
    onOpenMapPicker: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    val defaultLatitude = stringResource(R.string.address_map_default_latitude).toDouble()
    val defaultLongitude = stringResource(R.string.address_map_default_longitude).toDouble()
    val defaultZoom = stringResource(R.string.address_map_default_zoom).toFloat()

    val hasSelection = latitude != null && longitude != null
    val cameraTarget = if (latitude != null && longitude != null) {
        LatLng(latitude, longitude)
    } else {
        LatLng(defaultLatitude, defaultLongitude)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
     

        TextButton(
            onClick = onOpenMapPicker,
            enabled = enabled,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(text = stringResource(R.string.address_map_open_picker))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainerHigh,
            ),
        ) {
            if (!errorMessage.isNullOrBlank()) {
                MapErrorPlaceholder(errorMessage)
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    AddressGoogleMapView(
                        modifier = Modifier.fillMaxSize(),
                        target = cameraTarget,
                        zoom = defaultZoom,
                        marker = if (hasSelection) cameraTarget else null,
                        enabled = false,
                        onLocationPicked = { _, _ -> },
                        uiSettings = MapUiSettings(
                            compassEnabled = false,
                            indoorLevelPickerEnabled = false,
                            mapToolbarEnabled = false,
                            myLocationButtonEnabled = false,
                            rotationGesturesEnabled = false,
                            scrollGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            zoomControlsEnabled = false,
                            zoomGesturesEnabled = false,
                        ),
                    )

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorScheme.scrim.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = colorScheme.primary)
                        }
                    }
                }
            }
        }

        if (hasSelection && errorMessage.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        R.string.address_map_coordinates,
                        latitude,
                        longitude,
                    ),
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun MapErrorPlaceholder(message: String) {
    MapStatusPlaceholder(
        title = stringResource(R.string.address_map_error_title),
        subtitle = message,
        iconTint = MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun MapStatusPlaceholder(
    title: String,
    subtitle: String,
    iconTint: Color,
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = iconTint,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
