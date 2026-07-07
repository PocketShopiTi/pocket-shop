package com.iti.pocketshop.features.search.domain.model

import androidx.annotation.StringRes
import com.iti.pocketshop.R

enum class SortOption(@StringRes val labelId: Int) {
    RELEVANCE(R.string.search_sort_featured),
    PRICE_LOW_TO_HIGH(R.string.search_sort_price_asc),
    PRICE_HIGH_TO_LOW(R.string.search_sort_price_desc)
}
