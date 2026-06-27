package com.iti.pocketshop.features.wishlist

data class WishlistState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)