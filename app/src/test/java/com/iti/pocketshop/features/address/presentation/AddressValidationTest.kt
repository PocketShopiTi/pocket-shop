package com.iti.pocketshop.features.address.presentation

import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.AddressField
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
                address1 = "",
                city = "",
                zip = "",
                country = "",
            ),
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
                phone = "+14155550123",
                address1 = "24 Market Street",
                city = "San Francisco",
                zip = "94105",
                country = "United States",
            ),
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
                address1 = "24 Pemberton Gardens",
                city = "London",
                zip = "12345",
                country = "United Kingdom",
            ),
        )

        assertEquals("Enter a valid phone number for United Kingdom.", errors[AddressField.PHONE])
        assertEquals("Enter a valid postal code for United Kingdom.", errors[AddressField.ZIP])
    }
}
