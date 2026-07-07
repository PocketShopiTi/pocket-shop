package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
internal fun formatMoney(money: Money?): String = remember(money, Locale.getDefault()) {
    money?.let {
        runCatching {
            NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(it.currencyCode)
                maximumFractionDigits = if (it.amount % 1.0 == 0.0) 0 else 2
            }.format(it.amount)
        }.getOrDefault("${it.amount} ${it.currencyCode}")
    }.orEmpty()
}
