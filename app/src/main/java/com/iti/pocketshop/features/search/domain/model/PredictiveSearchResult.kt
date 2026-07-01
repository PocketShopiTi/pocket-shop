package com.iti.pocketshop.features.search.domain.model

data class PredictiveSearchResult(
    val products: List<PredictiveProduct>,
    val collections: List<PredictiveCollection>,
    val articles: List<PredictiveArticle>,
    val pages: List<PredictivePage>,
    val queries: List<SearchQuerySuggestion>
) {
    companion object {
        fun empty() = PredictiveSearchResult(
            products = emptyList(),
            collections = emptyList(),
            articles = emptyList(),
            pages = emptyList(),
            queries = emptyList()
        )
    }
}
data class PredictiveProduct(
    val id: String,
    val title: String,
    val handle: String,
    val imageUrl: String?,
    val imageAlt: String?,
    val price: Double,
    val currencyCode: String
)

data class PredictiveCollection(
    val id: String,
    val title: String,
    val handle: String,
    val imageUrl: String?,
    val imageAlt: String?
)

data class PredictiveArticle(
    val id: String,
    val title: String,
    val handle: String,
    val imageUrl: String?,
    val imageAlt: String?
)

data class PredictivePage(
    val id: String,
    val title: String,
    val handle: String
)

data class SearchQuerySuggestion(
    val text: String,
    val styledText: String,
    val trackingParameters: String?
)
