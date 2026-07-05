package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
internal fun AddressGoogleMapView(
    target: LatLng,
    zoom: Float,
    marker: LatLng?,
    enabled: Boolean,
    onLocationPicked: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
    uiSettings: MapUiSettings = MapUiSettings(),
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(target, zoom)
    }
    val useDarkMap = MaterialTheme.colorScheme.background.luminance() < DARK_THEME_LUMINANCE_THRESHOLD
    val mapProperties = remember(useDarkMap) {
        MapProperties(
            mapStyleOptions = if (useDarkMap) {
                MapStyleOptions(DARK_MAP_STYLE_JSON)
            } else {
                null
            },
        )
    }

    LaunchedEffect(target.latitude, target.longitude, zoom) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(target, zoom),
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapClick = { latLng ->
            if (enabled) {
                onLocationPicked(latLng.latitude, latLng.longitude)
            }
        },
    ) {
        marker?.let { latLng ->
            Marker(state = rememberUpdatedMarkerState(position = latLng))
        }
    }
}

private const val DARK_THEME_LUMINANCE_THRESHOLD = 0.5f

private const val DARK_MAP_STYLE_JSON = """
[
  {
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#1f1f1f"
      }
    ]
  },
  {
    "elementType": "labels.icon",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#d6d6d6"
      }
    ]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#1f1f1f"
      }
    ]
  },
  {
    "featureType": "administrative",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#5f6368"
      }
    ]
  },
  {
    "featureType": "poi",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#2d2f31"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#3a3d40"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [
      {
        "color": "#242628"
      }
    ]
  },
  {
    "featureType": "transit",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#2d2f31"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#17263c"
      }
    ]
  }
]
"""
