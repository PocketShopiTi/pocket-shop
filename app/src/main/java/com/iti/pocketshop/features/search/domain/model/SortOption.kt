package com.iti.pocketshop.features.search.domain.model

enum class SortOption(val label: String) {
    RELEVANCE("Featured"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low")
}
