package com.iti.pocketshop.features.categories.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.categories.domain.models.CategoryItem

interface CategoriesRepository {
    suspend fun getCategories(
        first: Int = 20
    ): PocketResult<List<CategoryItem>, PocketDataError.Remote>
}
