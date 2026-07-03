package com.iti.pocketshop.features.categories.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.categories.domain.models.CategoryItem
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository,
) {
    suspend operator fun invoke(
        first: Int = 50,
    ): PocketResult<List<CategoryItem>, PocketDataError.Remote> {
        return repository.getCategories(first = first)
    }
}
