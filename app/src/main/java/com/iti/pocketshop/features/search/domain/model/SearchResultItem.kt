package com.iti.pocketshop.features.search.domain.model

sealed class SearchResultItem {
    data class ProductItem(
        val id: String,
        val title: String,
        val handle: String,
        val imageUrl: String?,
        val imageAlt: String?,
        val price: Double,
        val currencyCode: String,
        val vendor: String,
        val productType: String,
        val tags: List<String>,
        val options: List<ProductSearchOption>
    ) : SearchResultItem()

    data class ArticleItem(
        val id: String,
        val title: String,
        val handle: String,
        val excerpt: String?,
        val imageUrl: String?,
        val imageAlt: String?
    ) : SearchResultItem()

    data class PageItem(
        val id: String,
        val title: String,
        val handle: String
    ) : SearchResultItem()
}

data class ProductSearchOption(
    val name: String,
    val values: List<String>
)
