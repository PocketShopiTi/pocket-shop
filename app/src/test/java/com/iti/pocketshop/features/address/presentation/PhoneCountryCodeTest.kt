package com.iti.pocketshop.features.address.presentation

import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.utils.PhoneCountryCode
import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneCountryCodeTest {

    @Test
    fun `country resources are wired correctly`() {
        assertEquals(R.string.address_phone_country_united_states, PhoneCountryCode.UNITED_STATES.labelResId)
        assertEquals(R.string.address_phone_country_canada, PhoneCountryCode.CANADA.labelResId)
        assertEquals(R.string.address_phone_country_flag_united_states, PhoneCountryCode.UNITED_STATES.flagResId)
        assertEquals(R.string.address_phone_country_flag_canada, PhoneCountryCode.CANADA.flagResId)
    }

    @Test
    fun `from saved phone prefers explicit country metadata`() {
        assertEquals(
            PhoneCountryCode.CANADA,
            PhoneCountryCode.fromSavedPhone(
                phone = "+15551234567",
                countryCode = "CA",
                country = "Canada",
            ),
        )
    }
}
