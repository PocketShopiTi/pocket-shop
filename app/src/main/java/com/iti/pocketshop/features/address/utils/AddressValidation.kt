package com.iti.pocketshop.features.address.utils

import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.AddressField
import com.iti.pocketshop.features.address.presentation.state.PhoneCountryCode
import java.util.Locale

internal fun validateAddressEditor(
    editor: AddressEditorState,
    strings: AddressValidationStrings,
): Map<AddressField, String> {
    val errors = linkedMapOf<AddressField, String>()

    if (editor.firstName.isBlank()) {
        errors[AddressField.FIRST_NAME] = strings.firstNameRequired
    }
    if (editor.lastName.isBlank()) {
        errors[AddressField.LAST_NAME] = strings.lastNameRequired
    }
    if (editor.address1.isBlank()) {
        errors[AddressField.ADDRESS1] = strings.streetAddressRequired
    }
    if (editor.city.isBlank()) {
        errors[AddressField.CITY] = strings.cityRequired
    }
    if (editor.country.isBlank()) {
        errors[AddressField.COUNTRY] = strings.countryRequired
    }

    validatePhone(editor.phoneCountryCode, editor.phone, strings)?.let { message ->
        errors[AddressField.PHONE] = message
    }

    validatePostalCode(editor.country, editor.zip, strings)?.let { message ->
        errors[AddressField.ZIP] = message
    }

    return errors
}

private fun validatePhone(
    phoneCountryCode: PhoneCountryCode,
    phone: String,
    strings: AddressValidationStrings,
): String? {
    val trimmed = phone.trim()
    if (trimmed.isBlank()) {
        return strings.phoneRequired
    }

    return if (phoneCountryCode.matches(trimmed)) {
        null
    } else {
        when (phoneCountryCode) {
            PhoneCountryCode.INTERNATIONAL -> strings.internationalPhoneInvalid
            else -> strings.phoneInvalidFor(strings.phoneCountryLabel(phoneCountryCode))
        }
    }
}

private fun validatePostalCode(
    country: String,
    postalCode: String,
    strings: AddressValidationStrings,
): String? {
    val trimmed = postalCode.trim()
    val normalized = normalizePostalCode(trimmed)
    val postalCountry = resolvePostalCountry(country)

    if (trimmed.isBlank()) {
        return strings.postalCodeRequired
    }

    val isValid = when (postalCountry) {
        PostalCountry.UNITED_STATES ->
            Regex("^\\d{5}(\\d{4})?$").matches(normalized)

        PostalCountry.CANADA ->
            Regex("^[A-Z]\\d[A-Z]\\d[A-Z]\\d$").matches(normalized)

        PostalCountry.UNITED_KINGDOM ->
            Regex("^(GIR0AA|[A-Z]{1,2}\\d[A-Z\\d]?\\d[A-Z]{2})$").matches(normalized)

        PostalCountry.EGYPT,
        PostalCountry.SAUDI_ARABIA ->
            Regex("^\\d{5}$").matches(normalized)

        null ->
            normalized.length >= 3 && normalized.any(Char::isLetterOrDigit)
    }

    return if (isValid) {
        null
    } else {
        strings.postalCodeInvalidFor(countryLabel(country, strings))
    }
}

private fun normalizePostalCode(postalCode: String): String {
    return buildString(postalCode.length) {
        postalCode.forEach { char ->
            when {
                char.isDigit() -> {
                    val digit = Character.getNumericValue(char)
                    if (digit in 0..9) {
                        append(digit)
                    }
                }

                char.isLetter() -> append(char.uppercaseChar())
            }
        }
    }
}

private fun resolvePostalCountry(country: String): PostalCountry? {
    val trimmed = country.trim()
    val normalized = trimmed
        .lowercase(Locale.US)
        .replace(Regex("[^a-z0-9]+"), " ")
        .trim()

    return when {
        normalized in setOf("united states", "usa", "us", "united states of america") ||
                trimmed in setOf("الولايات المتحدة", "الولايات المتحدة الامريكية") ->
            PostalCountry.UNITED_STATES

        normalized in setOf("canada", "ca") ||
                trimmed == "كندا" ->
            PostalCountry.CANADA

        normalized in setOf("united kingdom", "uk", "great britain", "britain") ||
                trimmed in setOf("المملكة المتحدة", "بريطانيا") ->
            PostalCountry.UNITED_KINGDOM

        normalized in setOf("egypt", "eg", "arab republic of egypt") ||
                trimmed in setOf("مصر", "جمهورية مصر العربية") ->
            PostalCountry.EGYPT

        normalized in setOf("saudi arabia", "sa", "kingdom of saudi arabia") ||
                trimmed in setOf("السعودية", "المملكة العربية السعودية") ->
            PostalCountry.SAUDI_ARABIA

        else -> null
    }
}

private fun countryLabel(country: String, strings: AddressValidationStrings): String {
    return country.trim().ifBlank { strings.selectedCountryFallback }
}

private enum class PostalCountry {
    UNITED_STATES,
    CANADA,
    UNITED_KINGDOM,
    EGYPT,
    SAUDI_ARABIA,
}