package com.iti.pocketshop.features.address.data.datasource

import com.iti.pocketshop.features.address.domain.model.LocationCoordinates

interface CurrentLocationDataSource {
    suspend fun getCurrentLocation(): LocationCoordinates?
}
