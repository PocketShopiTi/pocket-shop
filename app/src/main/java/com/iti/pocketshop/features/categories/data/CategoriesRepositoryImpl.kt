package com.iti.pocketshop.features.categories.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.categories.domain.CategoriesRemoteSource
import com.iti.pocketshop.features.categories.domain.CategoriesRepository
import com.iti.pocketshop.features.categories.domain.models.CategoryItem
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val remoteSource: CategoriesRemoteSource
) : CategoriesRepository {
    override suspend fun getCategories(first: Int): PocketResult<List<CategoryItem>, PocketDataError.Remote> {
        return remoteSource.getCategories(first)
    }
}
