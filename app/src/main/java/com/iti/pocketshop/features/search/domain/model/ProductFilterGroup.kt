package com.iti.pocketshop.features.search.domain.model

data class ProductFilterGroup(
    val id: String,
    val label: String,
    val type: String,
    val values: List<ProductFilterValue>
)

data class ProductFilterValue(
    val id: String,
    val label: String,
    val count: Int,
    val input: String
)

fun ProductFilterValue.hasSameSelectionAs(other: ProductFilterValue): Boolean {
    return id == other.id || input == other.input
}

fun Iterable<ProductFilterValue>.containsSelection(value: ProductFilterValue): Boolean {
    return any { it.hasSameSelectionAs(value) }
}
