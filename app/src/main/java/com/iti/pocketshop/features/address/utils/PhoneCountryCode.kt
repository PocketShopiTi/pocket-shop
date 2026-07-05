package com.iti.pocketshop.features.address.utils

import android.content.Context
import androidx.annotation.StringRes
import com.iti.pocketshop.R
import java.util.Locale

enum class PhoneCountryCode(
    @StringRes val labelResId: Int,
    @StringRes val flagResId: Int,
    val dialingCode: String,
    private val localPrefix: String,
    private val nationalNumberLength: Int,
    private val countryNames: Set<String>,
    private val countryCodes: Set<String>,
) {
    UNITED_STATES(
        labelResId = R.string.address_phone_country_united_states,
        flagResId = R.string.address_phone_country_flag_united_states,
        dialingCode = "+1",
        localPrefix = "",
        nationalNumberLength = 10,
        countryNames = setOf(
            "united states",
            "usa",
            "us",
            "united states of america",
        ),
        countryCodes = setOf("US"),
    ),
    CANADA(
        labelResId = R.string.address_phone_country_canada,
        flagResId = R.string.address_phone_country_flag_canada,
        dialingCode = "+1",
        localPrefix = "",
        nationalNumberLength = 10,
        countryNames = setOf(
            "canada",
            "ca",
        ),
        countryCodes = setOf("CA"),
    ),
    UNITED_KINGDOM(
        labelResId = R.string.address_phone_country_united_kingdom,
        flagResId = R.string.address_phone_country_flag_united_kingdom,
        dialingCode = "+44",
        localPrefix = "0",
        nationalNumberLength = 10,
        countryNames = setOf(
            "united kingdom",
            "uk",
            "great britain",
            "britain",
        ),
        countryCodes = setOf("GB", "UK"),
    ),
    EGYPT(
        labelResId = R.string.address_phone_country_egypt,
        flagResId = R.string.address_phone_country_flag_egypt,
        dialingCode = "+20",
        localPrefix = "0",
        nationalNumberLength = 10,
        countryNames = setOf(
            "egypt",
            "eg",
            "arab republic of egypt",
        ),
        countryCodes = setOf("EG"),
    ),
    SAUDI_ARABIA(
        labelResId = R.string.address_phone_country_saudi_arabia,
        flagResId = R.string.address_phone_country_flag_saudi_arabia,
        dialingCode = "+966",
        localPrefix = "0",
        nationalNumberLength = 9,
        countryNames = setOf(
            "saudi arabia",
            "sa",
            "kingdom of saudi arabia",
        ),
        countryCodes = setOf("SA"),
    ),
    INTERNATIONAL(
        labelResId = R.string.address_phone_country_international,
        flagResId = R.string.address_phone_country_flag_international,
        dialingCode = "",
        localPrefix = "",
        nationalNumberLength = 0,
        countryNames = emptySet(),
        countryCodes = emptySet(),
    ),
    ;

    fun label(context: Context): String {
        return context.getString(labelResId)
    }

    fun flagEmoji(context: Context): String {
        return context.getString(flagResId)
    }

    fun displayLabel(context: Context): String {
        val label = label(context)
        return if (dialingCode.isBlank()) {
            label
        } else {
            "$label ($dialingCode)"
        }
    }


    fun displayNumber(phone: String): String {
        val normalized = normalizePhoneInput(phone)
        if (normalized.isBlank()) {
            return ""
        }
        if (this == INTERNATIONAL) {
            return normalized
        }

        return nationalNumber(normalized).ifBlank { normalized }
    }

    fun toE164(phone: String): String? {
        val normalized = normalizePhoneInput(phone)
        if (normalized.isBlank()) {
            return null
        }
        if (this == INTERNATIONAL) {
            return normalizeInternational(normalized)
        }

        val national = nationalNumber(normalized)
        if (national.length != nationalNumberLength) {
            return null
        }
        return "$dialingCode$national"
    }


    private fun nationalNumber(normalized: String): String {
        val hasPlus = normalized.startsWith("+")
        var digits = normalized.removePrefix("+")
        val codeDigits = dialingCode.removePrefix("+")

        if (codeDigits.isNotEmpty() && digits.startsWith(codeDigits) &&
            (hasPlus || digits.length == codeDigits.length + nationalNumberLength)
        ) {
            digits = digits.drop(codeDigits.length)
        }

        if (localPrefix.isNotEmpty() && digits.startsWith(localPrefix) &&
            digits.length == localPrefix.length + nationalNumberLength
        ) {
            digits = digits.drop(localPrefix.length)
        }

        return digits
    }

    fun matches(phone: String): Boolean {
        return toE164(phone) != null
    }

    fun withCountryCodeFallback(country: String): PhoneCountryCode {
        return fromCountry(country)
    }

    companion object {
        private val E164_REGEX = Regex("^\\+[1-9]\\d{7,14}$")

        fun default(): PhoneCountryCode {
            return fromLocale(Locale.getDefault())
        }

        fun fromLocale(locale: Locale): PhoneCountryCode {
            return fromCountryCode(locale.country)
                ?: fromCountry(locale.displayCountry)
        }

        fun fromCountry(country: String): PhoneCountryCode {
            val normalized = normalizeCountryKey(country)
            if (normalized.isBlank()) {
                return defaultFromLocale()
            }

            return entries.firstOrNull { option ->
                normalized in option.countryNames || normalized in option.countryCodes.map { it.lowercase(
                    Locale.US) }
            } ?: defaultFromLocale()
        }

        fun fromCountryCode(countryCode: String): PhoneCountryCode? {
            val normalized = countryCode.trim().uppercase(Locale.US)
            if (normalized.isBlank()) {
                return null
            }

            return entries.firstOrNull { option ->
                normalized in option.countryCodes
            }
        }

        fun fromSavedPhone(
            phone: String,
            countryCode: String = "",
            country: String = "",
        ): PhoneCountryCode {
            fromCountryCode(countryCode)?.let { return it }
            country.takeIf { it.isNotBlank() }?.let { nonBlankCountry ->
                return fromCountry(nonBlankCountry)
            }
            detectFromPhone(phone)?.let { return it }
            return defaultFromLocale()
        }

        private fun detectFromPhone(phone: String): PhoneCountryCode? {
            val normalized = normalizePhoneInput(phone)
            return when {
                normalized.startsWith("+1") -> UNITED_STATES
                normalized.startsWith("+44") || normalized.startsWith("44") -> UNITED_KINGDOM
                normalized.startsWith("+20") || normalized.startsWith("20") -> EGYPT
                normalized.startsWith("+966") || normalized.startsWith("966") -> SAUDI_ARABIA
                E164_REGEX.matches(normalized) -> INTERNATIONAL
                else -> null
            }
        }

        private fun defaultFromLocale(): PhoneCountryCode {
            return fromCountryCode(Locale.getDefault().country) ?: UNITED_STATES
        }

        private fun normalizePhoneInput(phone: String): String {
            return phone
                .replace(Regex("[\\s\\-().]"), "")
                .trim()
        }

        private fun normalizeCountryKey(country: String): String {
            return country
                .trim()
                .lowercase(Locale.US)
                .replace(Regex("[^a-z0-9]+"), " ")
                .trim()
        }
    }

    private fun normalizeInternational(normalized: String): String? {
        return if (E164_REGEX.matches(normalized)) normalized else null
    }
}