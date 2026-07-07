package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

@Composable
internal fun formatDate(review: ProductReview): String =
    remember(review.date, Locale.getDefault()) {
        java.time.LocalDate.of(
            review.date.year,
            review.date.monthNumber,
            review.date.dayOfMonth,
        ).format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault()),
        )
    }
