package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.iti.pocketshop.LocalSettingsUser
import com.iti.pocketshop.core.pricing.PriceFormatter
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
internal fun formatMoney(money: Money?): String {
    val userSettings = LocalSettingsUser.current
    return remember(money, userSettings) {
        money?.let {
            PriceFormatter.format(
                amount = it.amount,
                sourceCurrencyCode = it.currencyCode,
                userSettings = userSettings,
            )
        }.orEmpty()
    }
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
