package com.iti.pocketshop.features.search.data.mapper
import com.iti.pocketshop.features.search.domain.model.PredictiveArticle
import com.iti.pocketshop.features.search.domain.model.PredictiveCollection
import com.iti.pocketshop.features.search.domain.model.PredictivePage
import com.iti.pocketshop.features.search.domain.model.PredictiveProduct
import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import com.iti.pocketshop.features.search.domain.model.ProductFilterGroup
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.ProductSearchOption
import com.iti.pocketshop.features.search.domain.model.SearchQuerySuggestion
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.shopify.PredictiveSearchQuery
import com.iti.pocketshop.shopify.SearchQuery
import org.json.JSONArray
import org.json.JSONObject

fun PredictiveSearchQuery.Data.toDomain(): PredictiveSearchResult {
    return PredictiveSearchResult(
        products = predictiveSearch?.products?.map { it.toDomain() } ?: emptyList(),
        collections = predictiveSearch?.collections?.map { it.toDomain() } ?: emptyList(),
        articles = predictiveSearch?.articles?.map { it.toDomain() } ?: emptyList(),
        pages = predictiveSearch?.pages?.map { it.toDomain() } ?: emptyList(),
        queries = predictiveSearch?.queries?.map { it.toDomain() } ?: emptyList()
    )
}

private fun PredictiveSearchQuery.Product.toDomain(): PredictiveProduct {
    return PredictiveProduct(
        id = id,
        title = title,
        handle = handle,
        imageUrl = featuredImage?.url,
        imageAlt = featuredImage?.altText,
        price = priceRange.minVariantPrice.amount,
        currencyCode = priceRange.minVariantPrice.currencyCode.name
    )
}

private fun PredictiveSearchQuery.Collection.toDomain(): PredictiveCollection {
    return PredictiveCollection(
        id = id,
        title = title,
        handle = handle,
        imageUrl = image?.url,
        imageAlt = image?.altText
    )
}

private fun PredictiveSearchQuery.Article.toDomain(): PredictiveArticle {
    return PredictiveArticle(
        id = id,
        title = title,
        handle = handle,
        imageUrl = image?.url,
        imageAlt = image?.altText
    )
}

private fun PredictiveSearchQuery.Page.toDomain(): PredictivePage {
    return PredictivePage(
        id = id,
        title = title,
        handle = handle
    )
}

private fun PredictiveSearchQuery.Query.toDomain(): SearchQuerySuggestion {
    return SearchQuerySuggestion(
        text = text,
        styledText = styledText,
        trackingParameters = trackingParameters
    )
}

fun SearchQuery.Data.toDomain(): SearchResult {
    val items = search.nodes.mapNotNull { it.toDomain() }
    val shopifyFilters = search.productFilters.map { it.toDomain() }

    return SearchResult(
        items = items,
        totalCount = search.totalCount,
        hasNextPage = search.pageInfo.hasNextPage,
        endCursor = search.pageInfo.endCursor,
        filters = shopifyFilters.withFallbackProductFilters(
            products = items.filterIsInstance<SearchResultItem.ProductItem>()
        )
    )
}

private fun SearchQuery.Node.toDomain(): SearchResultItem? {
    return when {
        onProduct != null -> SearchResultItem.ProductItem(
            id = onProduct.id,
            title = onProduct.title,
            handle = onProduct.handle,
            imageUrl = onProduct.featuredImage?.url,
            imageAlt = onProduct.featuredImage?.altText,
            price = onProduct.priceRange.minVariantPrice.amount,
            currencyCode = onProduct.priceRange.minVariantPrice.currencyCode.name,
            vendor = onProduct.vendor,
            productType = onProduct.productType,
            tags = onProduct.tags,
            options = onProduct.options.map { option ->
                ProductSearchOption(
                    name = option.name,
                    values = option.values
                )
            }
        )
        onArticle != null -> SearchResultItem.ArticleItem(
            id = onArticle.id,
            title = onArticle.title,
            handle = onArticle.handle,
            excerpt = onArticle.excerpt,
            imageUrl = onArticle.image?.url,
            imageAlt = onArticle.image?.altText
        )
        onPage != null -> SearchResultItem.PageItem(
            id = onPage.id,
            title = onPage.title,
            handle = onPage.handle
        )
        else -> null
    }
}

private fun SearchQuery.ProductFilter.toDomain(): ProductFilterGroup {
    return ProductFilterGroup(
        id = id,
        label = label,
        type = type.toString(),
        values = values.map { it.toDomain() }
    )
}

private fun SearchQuery.Value.toDomain(): ProductFilterValue {
    return ProductFilterValue(
        id = id,
        label = label,
        count = count,
        input = input.toFilterInputJson()
    )
}

private fun List<ProductFilterGroup>.withFallbackProductFilters(
    products: List<SearchResultItem.ProductItem>
): List<ProductFilterGroup> {
    if (products.isEmpty()) return this

    val fallbackFilters = buildList {
        if (!hasFilterInput("productVendor")) {
            products.toSimpleFilterGroup(
                id = "fallback:productVendor",
                label = "Vendor",
                inputField = "productVendor",
                valuesForProduct = { listOf(it.vendor) }
            )?.let(::add)
        }

        if (!hasFilterInput("productType")) {
            products.toSimpleFilterGroup(
                id = "fallback:productType",
                label = "Product Type",
                inputField = "productType",
                valuesForProduct = { listOf(it.productType) }
            )?.let(::add)
        }

        if (!hasFilterInput("tag")) {
            products.toSimpleFilterGroup(
                id = "fallback:tag",
                label = "Tags",
                inputField = "tag",
                valuesForProduct = { it.tags }
            )?.let(::add)
        }

        products.toVariantOptionFilterGroups(
            skipOption = { optionName -> hasVariantOptionFilter(optionName) }
        ).forEach(::add)
    }

    return this + fallbackFilters
}

private fun List<SearchResultItem.ProductItem>.toSimpleFilterGroup(
    id: String,
    label: String,
    inputField: String,
    valuesForProduct: (SearchResultItem.ProductItem) -> List<String>
): ProductFilterGroup? {
    val valueCounts = linkedMapOf<String, Int>()

    forEach { product ->
        valuesForProduct(product)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .forEach { value ->
                valueCounts[value] = (valueCounts[value] ?: 0) + 1
            }
    }

    val values = valueCounts.entries
        .sortedBy { it.key.lowercase() }
        .map { (value, count) ->
            ProductFilterValue(
                id = "$id:$value",
                label = value,
                count = count,
                input = JSONObject().put(inputField, value).toString()
            )
        }

    return values.takeIf { it.isNotEmpty() }?.let {
        ProductFilterGroup(
            id = id,
            label = label,
            type = "LIST",
            values = it
        )
    }
}

private fun List<SearchResultItem.ProductItem>.toVariantOptionFilterGroups(
    skipOption: (String) -> Boolean
): List<ProductFilterGroup> {
    val optionCounts = sortedMapOf<String, MutableMap<String, Int>>(String.CASE_INSENSITIVE_ORDER)

    forEach { product ->
        product.options.forEach optionLoop@ { option ->
            val optionName = option.name.trim()
            if (optionName.isBlank() || skipOption(optionName)) return@optionLoop

            val valueCounts = optionCounts.getOrPut(optionName) {
                sortedMapOf(String.CASE_INSENSITIVE_ORDER)
            }

            option.values
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinctBy { it.lowercase() }
                .forEach { value ->
                    valueCounts[value] = (valueCounts[value] ?: 0) + 1
                }
        }
    }

    return optionCounts.mapNotNull { (optionName, valueCounts) ->
        val values = valueCounts.map { (value, count) ->
            ProductFilterValue(
                id = "fallback:variantOption:$optionName:$value",
                label = value,
                count = count,
                input = JSONObject()
                    .put(
                        "variantOption",
                        JSONObject()
                            .put("name", optionName)
                            .put("value", value)
                    )
                    .toString()
            )
        }

        values.takeIf { it.isNotEmpty() }?.let {
            ProductFilterGroup(
                id = "fallback:variantOption:$optionName",
                label = optionName,
                type = "LIST",
                values = it
            )
        }
    }
}

private fun List<ProductFilterGroup>.hasFilterInput(field: String): Boolean {
    return any { group ->
        group.values.any { value ->
            value.input.hasJsonField(field)
        }
    }
}

private fun List<ProductFilterGroup>.hasVariantOptionFilter(optionName: String): Boolean {
    return any { group ->
        group.values.any { value ->
            value.input.variantOptionName()?.equals(optionName, ignoreCase = true) == true
        }
    }
}

private fun String.hasJsonField(field: String): Boolean {
    return runCatching { JSONObject(this).has(field) }.getOrDefault(false)
}

private fun String.variantOptionName(): String? {
    return runCatching {
        JSONObject(this).optJSONObject("variantOption")?.optString("name")?.takeIf { it.isNotBlank() }
    }.getOrNull()
}

private fun Any?.toFilterInputJson(): String {
    return when (this) {
        is String -> this
        is JSONObject,
        is JSONArray -> toString()
        is Map<*, *> -> JSONObject(toJsonMap()).toString()
        is Iterable<*> -> JSONArray(map { it.toJsonValue() }).toString()
        is Array<*> -> JSONArray(map { it.toJsonValue() }).toString()
        else -> toString()
    }
}

private fun Map<*, *>.toJsonMap(): Map<String, Any?> {
    return entries.associate { (key, value) ->
        key.toString() to value.toJsonValue()
    }
}

private fun Any?.toJsonValue(): Any? {
    return when (this) {
        null -> JSONObject.NULL
        is Map<*, *> -> JSONObject(toJsonMap())
        is Iterable<*> -> JSONArray(map { it.toJsonValue() })
        is Array<*> -> JSONArray(map { it.toJsonValue() })
        else -> this
    }
}
