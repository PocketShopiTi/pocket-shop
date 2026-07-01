package com.iti.pocketshop.features.search.domain.model

import org.json.JSONException
import org.json.JSONObject

private const val PRICE_RANGE_FILTER_TYPE = "PRICE_RANGE"

fun List<ProductFilterGroup>.extractPriceRangeBounds(): ClosedFloatingPointRange<Float>? {
    val jsonInput = this
        .find { it.type == PRICE_RANGE_FILTER_TYPE }
        ?.values
        ?.firstOrNull()
        ?.input
        ?: return null

    return try {
        val priceObj = JSONObject(jsonInput).optJSONObject("price") ?: return null
        if (!priceObj.has("min") || !priceObj.has("max")) return null

        val min = priceObj.getDouble("min").toFloat()
        val max = priceObj.getDouble("max").toFloat()

        if (max > min) min..max else null
    } catch (e: JSONException) {
            e.printStackTrace()
            null
    }
}