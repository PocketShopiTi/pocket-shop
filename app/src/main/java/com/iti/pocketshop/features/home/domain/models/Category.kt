package com.iti.pocketshop.features.home.domain.models

import com.iti.pocketshop.R


data class Category(
    val id: String,
    val catName: CategoryName,
    val handle: String,
    val catLogoUrl: String?,
)

enum class CategoryName(
    val titleId: Int
) {
    Men(
        titleId = R.string.men
    ),
    Women(
        titleId = R.string.women
    ),
    Kids(
        titleId = R.string.kids
    ),
    Baby(
        titleId = R.string.baby
    ),
    Shoes(
        titleId = R.string.shoes
    ),
    Bags(
        titleId = R.string.bags
    ),
    Accessories(
        titleId = R.string.accessories
    ),
    Pets(
        titleId = R.string.pets
    );

    companion object {
        fun getTypeByString(text: String) : CategoryName {
            return CategoryName.entries.find { it.name.equals(text, ignoreCase = true) } ?: Men
        }
    }
}