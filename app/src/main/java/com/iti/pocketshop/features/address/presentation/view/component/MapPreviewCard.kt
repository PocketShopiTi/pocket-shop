package com.iti.pocketshop.features.address.presentation.view.component

import android.preference.PreferenceManager
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.iti.pocketshop.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


@Composable
internal fun MapPreviewCard(
    latitude: Double?,
    longitude: Double?,
    isLoading: Boolean,
    enabled: Boolean,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    errorMessage: String?,
    onLocationPicked: (Double, Double) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    val defaultLatitude = stringResource(R.string.address_map_default_latitude).toDouble()
    val defaultLongitude = stringResource(R.string.address_map_default_longitude).toDouble()
    val defaultZoom = stringResource(R.string.address_map_default_zoom).toDouble()

    val hasSelection = latitude != null && longitude != null
    val cameraTarget = if (latitude != null && longitude != null) {
        GeoPoint(latitude, longitude)
    } else {
        GeoPoint(defaultLatitude, defaultLongitude)
    }
    val mapHeight by animateDpAsState(
        targetValue = if (isExpanded) 320.dp else 180.dp,
        label = "mapHeight",
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(
            title = stringResource(R.string.address_map_title),
        )

        TextButton(
            onClick = onToggleExpanded,
            enabled = enabled,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(
                text = stringResource(
                    if (isExpanded) {
                        R.string.address_map_collapse
                    } else {
                        R.string.address_map_expand
                    },
                ),
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight),
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
                    OsmMapView(
                        modifier = Modifier.fillMaxSize(),
                        target = cameraTarget,
                        zoom = defaultZoom,
                        marker = if (hasSelection) cameraTarget else null,
                        enabled = enabled,
                        onLocationPicked = onLocationPicked,
                    )

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.12f)),
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
private fun OsmMapView(
    modifier: Modifier,
    target: GeoPoint,
    zoom: Double,
    marker: GeoPoint?,
    enabled: Boolean,
    onLocationPicked: (Double, Double) -> Unit,
) {
    val context = LocalContext.current

    val mapView = remember {

        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context),
        )
        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(zoom)
            controller.setCenter(target)
        }
    }

    val markerOverlay = remember(mapView) { Marker(mapView) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.overlays.add(
                MapEventsOverlay(
                    object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            if (enabled) {
                                onLocationPicked(p.latitude, p.longitude)
                            }
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    },
                ),
            )
            mapView.onResume() // Kickstart rendering immediately
            mapView
        },
        update = { view ->
            view.controller.setZoom(zoom)
            view.post { view.controller.setCenter(target) }

            if (marker != null) {
                markerOverlay.position = marker
                markerOverlay.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                if (!view.overlays.contains(markerOverlay)) {
                    view.overlays.add(markerOverlay)
                }
            } else {
                view.overlays.remove(markerOverlay)
            }
            view.invalidate()
        },
    )
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
