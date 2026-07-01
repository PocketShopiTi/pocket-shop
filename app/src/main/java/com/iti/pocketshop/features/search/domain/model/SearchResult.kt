package com.iti.pocketshop.features.search.domain.model

data class SearchResult(
    val items: List<SearchResultItem>,
    val totalCount: Int,
    val hasNextPage: Boolean,
    val endCursor: String?,
    val filters: List<ProductFilterGroup>
) {
    val products: List<SearchResultItem.ProductItem>
        get() = items.filterIsInstance<SearchResultItem.ProductItem>()

    companion object {
        fun empty() = SearchResult(
            items = emptyList(),
            totalCount = 0,
            hasNextPage = false,
            endCursor = null,
            filters = emptyList()
        )
    }
}