package com.iti.pocketshop.features.search.utils

internal fun formatSearchPrice(amount: Double, currencyCode: String): String {
    val symbol = when (currencyCode.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "EGP" -> "EGP "
        else -> "$currencyCode "
    }
    return "$symbol${"%.2f".format(amount)}"
}
