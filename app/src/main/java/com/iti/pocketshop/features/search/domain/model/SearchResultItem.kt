package com.iti.pocketshop.features.search.domain.model

sealed class SearchResultItem {
    abstract val id: String

    data class ProductItem(
        override val id: String,
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
        override val id: String,
        val title: String,
        val handle: String,
        val excerpt: String?,
        val imageUrl: String?,
        val imageAlt: String?
    ) : SearchResultItem()

    data class PageItem(
        override val id: String,
        val title: String,
        val handle: String
    ) : SearchResultItem()
}

data class ProductSearchOption(
    val name: String,
    val values: List<String>
)