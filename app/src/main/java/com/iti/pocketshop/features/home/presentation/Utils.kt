package com.iti.pocketshop.features.home.presentation

import com.iti.pocketshop.features.home.domain.models.Money


fun formatPrice(money: Money): String {
    val symbol = when (money.currencyCode.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "EGP" -> "EGP "
        else  -> "${money.currencyCode} "
    }
    return "$symbol${"%.2f".format(money.amount)}"
}