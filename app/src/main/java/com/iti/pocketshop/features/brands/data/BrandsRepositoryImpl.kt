package com.iti.pocketshop.features.brands.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.brands.domain.BrandsRemoteSource
import com.iti.pocketshop.features.brands.domain.BrandsRepository
import com.iti.pocketshop.features.brands.domain.models.BrandItem
import javax.inject.Inject

class BrandsRepositoryImpl @Inject constructor(
    private val remoteSource: BrandsRemoteSource
) : BrandsRepository {
    override suspend fun getBrands(first: Int): PocketResult<List<BrandItem>, PocketDataError.Remote> {
        return remoteSource.getBrands(first)
    }
}
