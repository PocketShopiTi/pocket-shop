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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun MapPreviewCard(
    latitude: Double?,
    longitude: Double?,
    isLoading: Boolean,
    enabled: Boolean,
    hasMapsKey: Boolean,
    onLocationPicked: (Double, Double) -> Unit,
) {
    val extendedColors = LocalExtendedColors.current
    val selectedLocation = latitude?.let { lat ->
        longitude?.let { lng ->
            LatLng(lat, lng)
        }
    }
    val defaultLocation = LatLng(
        stringResource(R.string.address_map_default_latitude).toDouble(),
        stringResource(R.string.address_map_default_longitude).toDouble(),
    )
    val defaultZoom = stringResource(R.string.address_map_default_zoom).toFloat()
    val cameraTarget = selectedLocation ?: defaultLocation

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.address_map_title),
            color = extendedColors.textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, extendedColors.outline),
            colors = CardDefaults.cardColors(
                containerColor = extendedColors.surface,
            ),
        ) {
            if (!hasMapsKey) {
                MapUnavailablePlaceholder()
            } else {
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(cameraTarget, defaultZoom)
                }

                LaunchedEffect(cameraTarget.latitude, cameraTarget.longitude) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(cameraTarget, defaultZoom)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            if (enabled) onLocationPicked(latLng.latitude, latLng.longitude)
                        },
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            mapToolbarEnabled = false,
                            myLocationButtonEnabled = false,
                        ),
                    ) {
                        selectedLocation?.let { location ->
                            Marker(state = MarkerState(position = location))
                        }
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = extendedColors.primary)
                        }
                    }
                }
            }
        }

        if (selectedLocation != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = extendedColors.success,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        R.string.address_map_coordinates,
                        selectedLocation.latitude,
                        selectedLocation.longitude,
                    ),
                    color = extendedColors.textSecondary,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun MapUnavailablePlaceholder() {
    val extendedColors = LocalExtendedColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(extendedColors.surfaceVariant),
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
                    .background(extendedColors.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = extendedColors.primary,
                )
            }
            Text(
                text = stringResource(R.string.address_map_picker_unavailable_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = extendedColors.textPrimary,
            )
            Text(
                text = stringResource(R.string.address_map_picker_unavailable_subtitle),
                color = extendedColors.textSecondary,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
            )
        }
    }
}
