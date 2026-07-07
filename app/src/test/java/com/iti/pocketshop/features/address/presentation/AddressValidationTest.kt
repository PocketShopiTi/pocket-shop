package com.iti.pocketshop.features.address.presentation

import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.AddressField
import com.iti.pocketshop.features.address.utils.PhoneCountryCode
import com.iti.pocketshop.features.address.utils.AddressValidationStrings
import com.iti.pocketshop.features.address.utils.validateAddressEditor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddressValidationTest {

    @Test
    fun `validateAddressEditor returns clear field errors for blank required inputs`() {
        val errors = validateAddressEditor(
            AddressEditorState(
                firstName = "",
                lastName = "",
                phone = "",
                phoneCountryCode = PhoneCountryCode.UNITED_STATES,
                address1 = "",
                city = "",
                zip = "",
                country = "",
            ),
            TestAddressValidationStrings,
        )

        assertEquals("First name is required.", errors[AddressField.FIRST_NAME])
        assertEquals("Last name is required.", errors[AddressField.LAST_NAME])
        assertEquals("Street address is required.", errors[AddressField.ADDRESS1])
        assertEquals("City is required.", errors[AddressField.CITY])
        assertEquals("Country is required.", errors[AddressField.COUNTRY])
        assertEquals("Phone number is required.", errors[AddressField.PHONE])
        assertEquals("Postal code is required.", errors[AddressField.ZIP])
    }

    @Test
    fun `validateAddressEditor accepts valid us address format`() {
        val errors = validateAddressEditor(
            AddressEditorState(
                firstName = "Lina",
                lastName = "Saleh",
                phone = "4155550123",
                phoneCountryCode = PhoneCountryCode.UNITED_STATES,
                address1 = "24 Market Street",
                city = "San Francisco",
                zip = "94105",
                country = "United States",
            ),
            TestAddressValidationStrings,
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validateAddressEditor validates country specific postal and phone rules`() {
        val errors = validateAddressEditor(
            AddressEditorState(
                firstName = "Safa",
                lastName = "Chen",
                phone = "12345",
                phoneCountryCode = PhoneCountryCode.UNITED_KINGDOM,
                address1 = "24 Pemberton Gardens",
                city = "London",
                zip = "12345",
                country = "United Kingdom",
            ),
            TestAddressValidationStrings,
        )

        assertEquals("Enter a valid phone number for United Kingdom.", errors[AddressField.PHONE])
        assertEquals("Enter a valid postal code for United Kingdom.", errors[AddressField.ZIP])
    }

    @Test
    fun `validateAddressEditor accepts egypt postal code with localized country name`() {
        val errors = validateAddressEditor(
            AddressEditorState(
                firstName = "Amina",
                lastName = "Hassan",
                phone = "01012345678",
                phoneCountryCode = PhoneCountryCode.EGYPT,
                address1 = "12 Nile Street",
                city = "Cairo",
                zip = "\u0661\u0662 \u0663\u0664\u0665",
                country = "\u0645\u0635\u0631",
            ),
            TestAddressValidationStrings,
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validateAddressEditor allows missing egypt postal code`() {
        val errors = validateAddressEditor(
            AddressEditorState(
                firstName = "Amina",
                lastName = "Hassan",
                phone = "01012345678",
                phoneCountryCode = PhoneCountryCode.EGYPT,
                address1 = "12 Nile Street",
                city = "Cairo",
                zip = "",
                country = "Egypt",
            ),
            TestAddressValidationStrings,
        )

        assertTrue(!errors.containsKey(AddressField.ZIP))
    }

    private object TestAddressValidationStrings : AddressValidationStrings {
        override val firstNameRequired: String = "First name is required."
        override val lastNameRequired: String = "Last name is required."
        override val streetAddressRequired: String = "Street address is required."
        override val cityRequired: String = "City is required."
        override val countryRequired: String = "Country is required."
        override val phoneRequired: String = "Phone number is required."
        override val internationalPhoneInvalid: String = "Enter a valid international phone number."
        override val postalCodeRequired: String = "Postal code is required."
        override val selectedCountryFallback: String = "the selected country"

        override fun phoneCountryLabel(countryCode: PhoneCountryCode): String {
            return when (countryCode) {
                PhoneCountryCode.UNITED_STATES -> "United States"
                PhoneCountryCode.CANADA -> "Canada"
                PhoneCountryCode.UNITED_KINGDOM -> "United Kingdom"
                PhoneCountryCode.EGYPT -> "Egypt"
                PhoneCountryCode.SAUDI_ARABIA -> "Saudi Arabia"
                PhoneCountryCode.INTERNATIONAL -> "International"
            }
        }

        override fun phoneInvalidFor(countryLabel: String): String {
            return "Enter a valid phone number for $countryLabel."
        }

        override fun postalCodeInvalidFor(countryLabel: String): String {
            return "Enter a valid postal code for $countryLabel."
        }
    }
}
