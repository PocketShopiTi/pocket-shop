package com.iti.pocketshop.features.brands.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.brands.domain.models.BrandItem
import javax.inject.Inject

class GetBrandsUseCase @Inject constructor(
    private val repository: BrandsRepository,
) {
    suspend operator fun invoke(
        first: Int = 50,
    ): PocketResult<List<BrandItem>, PocketDataError.Remote> {
        return repository.getBrands(first = first)
    }
}
