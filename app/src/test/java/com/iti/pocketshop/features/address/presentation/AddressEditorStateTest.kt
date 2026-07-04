package com.iti.pocketshop.features.address.presentation

import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.PhoneCountryCode
import org.junit.Assert.assertEquals
import org.junit.Test

class AddressEditorStateTest {

    @Test
    fun `toDraft combines phone country code with local phone number`() {
        val draft = AddressEditorState(
            firstName = "Amina",
            lastName = "Khaled",
            phone = "02012345678",
            phoneCountryCode = PhoneCountryCode.UNITED_KINGDOM,
            address1 = "14 West Square",
            city = "Manchester",
            zip = "M15 6AB",
            country = "United Kingdom",
        ).toDraft()

        assertEquals("+442012345678", draft.phone)
    }
}
