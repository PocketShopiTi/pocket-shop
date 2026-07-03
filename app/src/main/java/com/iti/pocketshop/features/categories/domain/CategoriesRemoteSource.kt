package com.iti.pocketshop.features.categories.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.categories.domain.models.CategoryItem

interface CategoriesRemoteSource {
    suspend fun getCategories(
        first: Int
    ): PocketResult<List<CategoryItem>, PocketDataError.Remote>
}
