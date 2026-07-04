package com.iti.pocketshop.features.address.utils

import android.content.Context
import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.presentation.state.PhoneCountryCode

internal interface AddressValidationStrings {
    val firstNameRequired: String
    val lastNameRequired: String
    val streetAddressRequired: String
    val cityRequired: String
    val countryRequired: String
    val phoneRequired: String
    val internationalPhoneInvalid: String
    val postalCodeRequired: String
    val selectedCountryFallback: String
    fun phoneCountryLabel(countryCode: PhoneCountryCode): String
    fun phoneInvalidFor(countryLabel: String): String
    fun postalCodeInvalidFor(countryLabel: String): String
}

internal class AndroidAddressValidationStrings(
    private val context: Context,
) : AddressValidationStrings {
    override val firstNameRequired: String
        get() = context.getString(R.string.address_validation_first_name_required)

    override val lastNameRequired: String
        get() = context.getString(R.string.address_validation_last_name_required)

    override val streetAddressRequired: String
        get() = context.getString(R.string.address_validation_street_address_required)

    override val cityRequired: String
        get() = context.getString(R.string.address_validation_city_required)

    override val countryRequired: String
        get() = context.getString(R.string.address_validation_country_required)

    override val phoneRequired: String
        get() = context.getString(R.string.address_validation_phone_required)

    override val internationalPhoneInvalid: String
        get() = context.getString(R.string.address_validation_phone_invalid_international)

    override val postalCodeRequired: String
        get() = context.getString(R.string.address_validation_postal_code_required)

    override val selectedCountryFallback: String
        get() = context.getString(R.string.address_validation_selected_country_fallback)

    override fun phoneCountryLabel(countryCode: PhoneCountryCode): String {
        return context.getString(countryCode.labelResId)
    }

    override fun phoneInvalidFor(countryLabel: String): String {
        return context.getString(R.string.address_validation_phone_invalid_for_country, countryLabel)
    }

    override fun postalCodeInvalidFor(countryLabel: String): String {
        return context.getString(R.string.address_validation_postal_code_invalid_for_country, countryLabel)
    }
}
