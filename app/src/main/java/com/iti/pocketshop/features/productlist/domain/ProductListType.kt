package com.iti.pocketshop.features.productlist.domain

import com.iti.pocketshop.R

enum class ProductListType(
    val titleResId: Int,
    val sortKey: String,
    val reverse: Boolean
) {
    FEATURED(
        titleResId = R.string.featured,
        sortKey = "ID",
        reverse = false
    ),
    TRENDING(
        titleResId = R.string.trending,
        sortKey = "BEST_SELLING",
        reverse = false
    ),
    NEW_ARRIVALS(
        titleResId = R.string.new_arrivals,
        sortKey = "CREATED_AT",
        reverse = true
    );

    companion object {
        fun fromString(value: String): ProductListType {
            return entries.first { it.name == value }
        }
    }
}
