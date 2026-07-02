package com.iti.pocketshop.features.address.utils

import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.AddressField
import java.util.Locale

internal fun validateAddressEditor(editor: AddressEditorState): Map<AddressField, String> {
    val errors = linkedMapOf<AddressField, String>()

    if (editor.firstName.isBlank()) {
        errors[AddressField.FIRST_NAME] = "First name is required."
    }
    if (editor.lastName.isBlank()) {
        errors[AddressField.LAST_NAME] = "Last name is required."
    }
    if (editor.address1.isBlank()) {
        errors[AddressField.ADDRESS1] = "Street address is required."
    }
    if (editor.city.isBlank()) {
        errors[AddressField.CITY] = "City is required."
    }
    if (editor.country.isBlank()) {
        errors[AddressField.COUNTRY] = "Country is required."
    }

    validatePhone(editor.country, editor.phone)?.let { message ->
        errors[AddressField.PHONE] = message
    }

    validatePostalCode(editor.country, editor.zip)?.let { message ->
        errors[AddressField.ZIP] = message
    }

    return errors
}

private fun validatePhone(country: String, phone: String): String? {
    val trimmed = phone.trim()
    if (trimmed.isBlank()) {
        return "Phone number is required."
    }

    val normalizedPhone = normalizePhone(trimmed)
    val countryKey = normalizeCountry(country)

    return when {
        countryKey in setOf("united states", "usa", "us", "united states of america", "canada", "ca") ->
            if (Regex("^\\+1\\d{10}$").matches(normalizedPhone) ||
                Regex("^1?\\d{10}$").matches(normalizedPhone)
            ) {
                null
            } else {
                "Enter a valid phone number for ${countryLabel(country)}."
            }

        countryKey in setOf("united kingdom", "uk", "great britain", "britain") ->
            if (Regex("^\\+44\\d{9,10}$").matches(normalizedPhone) ||
                Regex("^0\\d{9,10}$").matches(normalizedPhone)
            ) {
                null
            } else {
                "Enter a valid phone number for ${countryLabel(country)}."
            }

        countryKey in setOf("egypt", "eg", "arab republic of egypt") ->
            if (Regex("^\\+20\\d{10}$").matches(normalizedPhone) ||
                Regex("^01\\d{9}$").matches(normalizedPhone)
            ) {
                null
            } else {
                "Enter a valid phone number for ${countryLabel(country)}."
            }

        countryKey in setOf("saudi arabia", "sa", "kingdom of saudi arabia") ->
            if (Regex("^\\+9665\\d{8}$").matches(normalizedPhone) ||
                Regex("^05\\d{8}$").matches(normalizedPhone)
            ) {
                null
            } else {
                "Enter a valid phone number for ${countryLabel(country)}."
            }

        else ->
            if (Regex("^\\+[1-9]\\d{7,14}$").matches(normalizedPhone)) {
                null
            } else {
                "Enter a valid international phone number."
            }
    }
}

private fun validatePostalCode(country: String, postalCode: String): String? {
    val trimmed = postalCode.trim()
    if (trimmed.isBlank()) {
        return "Postal code is required."
    }

    val upper = trimmed.uppercase(Locale.US)
    val countryKey = normalizeCountry(country)

    val isValid = when {
        countryKey in setOf("united states", "usa", "us", "united states of america") ->
            Regex("^\\d{5}(-\\d{4})?$").matches(upper)

        countryKey in setOf("canada", "ca") ->
            Regex("^[A-Z]\\d[A-Z][ -]?\\d[A-Z]\\d$").matches(upper)

        countryKey in setOf("united kingdom", "uk", "great britain", "britain") ->
            Regex("^(GIR ?0AA|[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2})$").matches(upper)

        countryKey in setOf("egypt", "eg", "arab republic of egypt", "saudi arabia", "sa", "kingdom of saudi arabia") ->
            Regex("^\\d{5}$").matches(upper)

        else -> upper.length >= 3 && upper.any(Char::isLetterOrDigit)
    }

    return if (isValid) null else {
        "Enter a valid postal code for ${countryLabel(country)}."
    }
}

private fun normalizePhone(phone: String): String {
    return phone
        .replace(Regex("[\\s\\-().]"), "")
        .trim()
}

private fun normalizeCountry(country: String): String {
    return country
        .trim()
        .lowercase(Locale.US)
        .replace(Regex("[^a-z0-9]+"), " ")
        .trim()
}

private fun countryLabel(country: String): String {
    return country.trim().ifBlank { "the selected country" }
}
