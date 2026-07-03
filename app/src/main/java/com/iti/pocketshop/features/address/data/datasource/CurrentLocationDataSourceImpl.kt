package com.iti.pocketshop.features.address.data.datasource

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.iti.pocketshop.features.address.domain.model.LocationCoordinates
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Singleton
class CurrentLocationDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CurrentLocationDataSource {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override suspend fun getCurrentLocation(): LocationCoordinates? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            val tokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                tokenSource.token,
            ).awaitLocationCoordinates(tokenSource)
                ?: fusedLocationClient.lastLocation.awaitLocationCoordinates()
        } catch (e: SecurityException) {
            Log.w(TAG, "Missing location permission while reading current location.", e)
            null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read current location: ${e.message}", e)
            null
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    private suspend fun Task<Location>.awaitLocationCoordinates(
        tokenSource: CancellationTokenSource? = null,
    ): LocationCoordinates? {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                tokenSource?.cancel()
            }
            addOnSuccessListener { location ->
                if (continuation.isActive) {
                    continuation.resume(location?.toCoordinates())
                }
            }
            addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
            addOnCanceledListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }

    private fun Location.toCoordinates(): LocationCoordinates {
        return LocationCoordinates(
            latitude = latitude,
            longitude = longitude,
        )
    }

    private companion object {
        const val TAG = "CurrentLocationDataSource"
    }
}
