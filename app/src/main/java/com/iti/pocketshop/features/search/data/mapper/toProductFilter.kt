package com.iti.pocketshop.features.search.data.mapper

import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.shopify.type.CategoryFilter
import com.iti.pocketshop.shopify.type.MetafieldFilter
import com.iti.pocketshop.shopify.type.PriceRangeFilter
import com.iti.pocketshop.shopify.type.ProductFilter
import com.iti.pocketshop.shopify.type.SearchSortKeys
import com.iti.pocketshop.shopify.type.TaxonomyMetafieldFilter
import com.iti.pocketshop.shopify.type.VariantOptionFilter
import org.json.JSONObject

fun String.toProductFilter(): ProductFilter? {
    return try {
        val json = JSONObject(this)
        ProductFilter(
            available = if (json.has("available"))
                Optional.present(json.getBoolean("available"))
            else
                Optional.Absent,

            variantOption = json.optJSONObject("variantOption")?.let {
                Optional.present(
                    VariantOptionFilter(
                        name = it.getString("name"),
                        value = it.getString("value")
                    )
                )
            } ?: Optional.Absent,

            category = json.optJSONObject("category")?.toCategoryFilter()?.let {
                Optional.present(it)
            } ?: Optional.Absent,

            taxonomyMetafield = json.optJSONObject("taxonomyMetafield")?.toTaxonomyMetafieldFilter()
                ?.let { Optional.present(it) } ?: Optional.Absent,

            productVendor = if (json.has("productVendor"))
                Optional.present(json.getString("productVendor"))
            else
                Optional.Absent,

            productType = if (json.has("productType"))
                Optional.present(json.getString("productType"))
            else
                Optional.Absent,

            tag = if (json.has("tag"))
                Optional.present(json.getString("tag"))
            else
                Optional.Absent,

            price = json.optJSONObject("price")?.let {
                Optional.present(
                    PriceRangeFilter(
                        min = if (it.has("min"))
                            Optional.present(it.getDouble("min"))
                        else
                            Optional.Absent,

                        max = if (it.has("max"))
                            Optional.present(it.getDouble("max"))
                        else
                            Optional.Absent
                    )
                )
            } ?: Optional.Absent,

            productMetafield = json.optJSONObject("productMetafield")?.toMetafieldFilter()?.let {
                Optional.present(it)
            } ?: Optional.Absent,

            variantMetafield = json.optJSONObject("variantMetafield")?.toMetafieldFilter()?.let {
                Optional.present(it)
            } ?: Optional.Absent,
        )
    } catch (e: Exception) {
        null
    }
}

private fun JSONObject.toCategoryFilter(): CategoryFilter? {

    val id = optString("id").takeIf { it.isNotBlank() } ?: return null

    return CategoryFilter(id = id)
}

private fun JSONObject.toMetafieldFilter(): MetafieldFilter? {

    val namespace = optString("namespace").takeIf { it.isNotBlank() } ?: return null

    val key = optString("key").takeIf { it.isNotBlank() } ?: return null

    val value = optString("value").takeIf { it.isNotBlank() } ?: return null

    return MetafieldFilter(namespace = namespace, key = key, value = value)
}

private fun JSONObject.toTaxonomyMetafieldFilter(): TaxonomyMetafieldFilter? {

    val namespace = optString("namespace").takeIf { it.isNotBlank() } ?: return null

    val key = optString("key").takeIf { it.isNotBlank() } ?: return null

    val value = optString("value").takeIf { it.isNotBlank() } ?: return null

    return TaxonomyMetafieldFilter(namespace = namespace, key = key, value = value)
}

fun SearchSortKeys.Companion.safeValueOf(value: String): SearchSortKeys {
    return SearchSortKeys.entries.find { it.name == value } ?: SearchSortKeys.RELEVANCE
}
