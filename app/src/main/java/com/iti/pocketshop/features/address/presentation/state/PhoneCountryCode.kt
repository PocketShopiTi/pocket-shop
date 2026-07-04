package com.iti.pocketshop.features.address.presentation.state

import android.content.Context
import androidx.annotation.StringRes
import com.iti.pocketshop.R
import java.util.Locale

enum class PhoneCountryCode(
    @StringRes val labelResId: Int,
    @StringRes val flagResId: Int,
    val dialingCode: String,
    private val localPrefix: String,
    private val countryNames: Set<String>,
    private val countryCodes: Set<String>,
) {
    UNITED_STATES(
        labelResId = R.string.address_phone_country_united_states,
        flagResId = R.string.address_phone_country_flag_united_states,
        dialingCode = "+1",
        localPrefix = "",
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

        return when (this) {
            UNITED_STATES, CANADA -> displayNorthAmerica(normalized)
            UNITED_KINGDOM -> displayUnitedKingdom(normalized)
            EGYPT -> displayEgypt(normalized)
            SAUDI_ARABIA -> displaySaudi(normalized)
            INTERNATIONAL -> normalized
        }
    }

    fun toE164(phone: String): String? {
        val normalized = normalizePhoneInput(phone)
        if (normalized.isBlank()) {
            return null
        }

        return when (this) {
            UNITED_STATES, CANADA -> normalizeNorthAmerica(normalized)
            UNITED_KINGDOM -> normalizeUnitedKingdom(normalized)
            EGYPT -> normalizeEgypt(normalized)
            SAUDI_ARABIA -> normalizeSaudi(normalized)
            INTERNATIONAL -> normalizeInternational(normalized)
        }
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
                normalized in option.countryNames || normalized in option.countryCodes.map { it.lowercase(Locale.US) }
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

    private fun displayNorthAmerica(normalized: String): String {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+1") && digits.length == 11 -> digits.drop(1)
            digits.startsWith("1") && digits.length == 11 -> digits.drop(1)
            digits.length == 10 -> digits
            else -> normalized
        }
    }

    private fun normalizeNorthAmerica(normalized: String): String? {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+1") && digits.length == 11 -> "+$digits"
            digits.startsWith("1") && digits.length == 11 -> "+$digits"
            digits.length == 10 -> "+1$digits"
            else -> null
        }
    }

    private fun displayUnitedKingdom(normalized: String): String {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+44") && digits.length == 12 -> "0${digits.drop(2)}"
            digits.startsWith("44") && normalized.firstOrNull() != '+' && digits.length == 12 -> "0${digits.drop(2)}"
            digits.startsWith("0") && digits.length == 11 -> digits
            digits.length == 10 -> "0$digits"
            else -> normalized
        }
    }

    private fun normalizeUnitedKingdom(normalized: String): String? {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+44") && digits.length == 12 -> "+$digits"
            digits.startsWith("44") && normalized.firstOrNull() != '+' && digits.length == 12 -> "+$digits"
            digits.startsWith("0") && digits.length == 11 -> "+44${digits.drop(1)}"
            digits.length == 10 -> "+44$digits"
            else -> null
        }
    }

    private fun displayEgypt(normalized: String): String {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+20") && digits.length == 12 -> "0${digits.drop(2)}"
            digits.startsWith("20") && normalized.firstOrNull() != '+' && digits.length == 12 -> "0${digits.drop(2)}"
            digits.startsWith("0") && digits.length == 11 -> digits
            digits.length == 10 -> "0$digits"
            else -> normalized
        }
    }

    private fun normalizeEgypt(normalized: String): String? {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+20") && digits.length == 12 -> "+$digits"
            digits.startsWith("20") && normalized.firstOrNull() != '+' && digits.length == 12 -> "+$digits"
            digits.startsWith("0") && digits.length == 11 -> "+20${digits.drop(1)}"
            digits.length == 10 -> "+20$digits"
            else -> null
        }
    }

    private fun displaySaudi(normalized: String): String {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+966") && digits.length == 12 -> "0${digits.drop(3)}"
            digits.startsWith("966") && normalized.firstOrNull() != '+' && digits.length == 12 -> "0${digits.drop(3)}"
            digits.startsWith("0") && digits.length == 10 -> digits
            digits.length == 9 -> "0$digits"
            else -> normalized
        }
    }

    private fun normalizeSaudi(normalized: String): String? {
        val digits = normalized.removePrefix("+")
        return when {
            normalized.startsWith("+966") && digits.length == 12 -> "+$digits"
            digits.startsWith("966") && normalized.firstOrNull() != '+' && digits.length == 12 -> "+$digits"
            digits.startsWith("0") && digits.length == 10 -> "+966${digits.drop(1)}"
            digits.length == 9 -> "+966$digits"
            else -> null
        }
    }

    private fun normalizeInternational(normalized: String): String? {
        return if (E164_REGEX.matches(normalized)) normalized else null
    }
}
