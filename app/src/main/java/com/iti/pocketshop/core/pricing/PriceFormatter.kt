package com.iti.pocketshop.core.pricing

import com.iti.pocketshop.common.settings.domain.models.CurrencySetting
import com.iti.pocketshop.common.settings.domain.models.LanguageSetting
import com.iti.pocketshop.common.settings.domain.models.UserSettings
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

private const val USD = "USD"
private const val EGP = "EGP"
private const val USD_TO_EGP_RATE = 49.0

data class ConvertedMoney(
    val amount: Double,
    val currencyCode: String,
)

object PriceFormatter {
    fun format(
        amount: Double,
        sourceCurrencyCode: String,
        userSettings: UserSettings,
        minFractionDigits: Int = 0,
        maxFractionDigits: Int = 2,
    ): String {
        val targetCurrencyCode = userSettings.currency.name
        val converted = convert(amount, sourceCurrencyCode, targetCurrencyCode)
        val locale = userSettings.language.toLocale()
        val number = formatNumber(
            amount = converted.amount,
            locale = locale,
            minFractionDigits = minFractionDigits,
            maxFractionDigits = maxFractionDigits,
        )
        val symbol = symbolFor(
            currencyCode = converted.currencyCode,
            language = userSettings.language,
        )

        return when (converted.currencyCode.uppercase(Locale.US)) {
            USD -> "$symbol$number"
            EGP -> "$number $symbol"
            else -> "$number $symbol"
        }
    }

    fun convert(
        amount: Double,
        sourceCurrencyCode: String,
        targetCurrency: CurrencySetting,
    ): ConvertedMoney = convert(amount, sourceCurrencyCode, targetCurrency.name)

    fun convert(
        amount: Double,
        sourceCurrencyCode: String,
        targetCurrencyCode: String,
    ): ConvertedMoney {
        val source = sourceCurrencyCode.uppercase(Locale.US)
        val target = targetCurrencyCode.uppercase(Locale.US)
        val convertedAmount = when {
            source == target -> amount
            source == USD && target == EGP -> amount * USD_TO_EGP_RATE
            source == EGP && target == USD -> amount / USD_TO_EGP_RATE
            else -> amount
        }
        val convertedCode = if (source == USD || source == EGP) target else source
        return ConvertedMoney(
            amount = convertedAmount,
            currencyCode = convertedCode,
        )
    }

    fun toEgpPaymentMinorUnits(
        amount: Double,
        sourceCurrencyCode: String,
    ): Long {
        return (convert(amount, sourceCurrencyCode, EGP).amount * 100).roundToLong()
    }

    fun toEgpMoney(
        amount: Double,
        sourceCurrencyCode: String,
    ): ConvertedMoney = convert(amount, sourceCurrencyCode, EGP)

    private fun formatNumber(
        amount: Double,
        locale: Locale,
        minFractionDigits: Int,
        maxFractionDigits: Int,
    ): String {
        val formatter = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = minFractionDigits
            maximumFractionDigits = maxFractionDigits
        }
        return (formatter as? DecimalFormat)
            ?.apply { isGroupingUsed = true }
            ?.format(amount)
            ?: formatter.format(amount)
    }

    private fun symbolFor(
        currencyCode: String,
        language: LanguageSetting,
    ): String {
        return when (currencyCode.uppercase(Locale.US)) {
            USD -> "\u0024"
            EGP -> if (language == LanguageSetting.ARABIC) "ج م" else "EGP"
            else -> currencyCode.uppercase(Locale.US)
        }
    }

    private fun LanguageSetting.toLocale(): Locale = Locale.forLanguageTag(getCode())
}
