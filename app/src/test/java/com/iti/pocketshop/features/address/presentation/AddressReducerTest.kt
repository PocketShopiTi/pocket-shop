package com.iti.pocketshop.features.address.presentation

import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.AddressState
import com.iti.pocketshop.features.address.presentation.viewmodel.reduceAddressState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AddressReducerTest {

    @Test
    fun `add address opens a blank editor and clears dialog state`() {
        val state = AddressState(
            addresses = listOf(sampleAddress()),
            pendingDeleteAddressId = "addr-99",
            error = AddressError.Shopify(listOf("Old error")),
            message = "Saved",
        )

        val result = reduceAddressState(state, AddressAction.AddAddressClicked)

        assertTrue(result.editor.visible)
        assertFalse(result.editor.isEditing)
        assertFalse(result.editor.isDefault)
        assertNull(result.pendingDeleteAddressId)
        assertNull(result.error)
        assertNull(result.message)
    }

    @Test
    fun `edit address opens the editor with the selected address`() {
        val address = sampleAddress(
            id = "addr-2",
            firstName = "Lina",
            lastName = "Saleh",
            company = "Pocket",
            isDefault = true,
        )
        val state = AddressState(addresses = listOf(sampleAddress(), address))

        val result = reduceAddressState(state, AddressAction.EditAddressClicked(address.id))

        assertEquals(AddressEditorState.fromAddress(address), result.editor)
        assertNull(result.pendingDeleteAddressId)
        assertNull(result.error)
        assertNull(result.message)
    }

    @Test
    fun `toggle default flips the switch state`() {
        val state = AddressState(
            editor = AddressEditorState.blank(isDefault = false).copy(visible = true),
        )

        val result = reduceAddressState(state, AddressAction.ToggleDefault)

        assertTrue(result.editor.isDefault)
        assertTrue(result.editor.visible)
    }

    @Test
    fun `delete clicked opens the confirmation dialog`() {
        val state = AddressState(addresses = listOf(sampleAddress()))

        val result = reduceAddressState(state, AddressAction.DeleteClicked("addr-1"))

        assertEquals("addr-1", result.pendingDeleteAddressId)
        assertNull(result.error)
        assertNull(result.message)
    }

    @Test
    fun `close editor clears form and transient state`() {
        val state = AddressState(
            editor = AddressEditorState.fromAddress(sampleAddress()).copy(visible = true),
            pendingDeleteAddressId = "addr-9",
            error = AddressError.Shopify(listOf("Oops")),
            message = "Saved",
        )

        val result = reduceAddressState(state, AddressAction.CloseEditor)

        assertFalse(result.editor.visible)
        assertFalse(result.editor.isEditing)
        assertNull(result.pendingDeleteAddressId)
        assertNull(result.error)
        assertNull(result.message)
    }

    private fun sampleAddress(
        id: String = "addr-1",
        firstName: String = "Safa",
        lastName: String = "Chen",
        company: String = "",
        isDefault: Boolean = false,
    ): Address {
        return Address(
            id = id,
            firstName = firstName,
            lastName = lastName,
            company = company,
            phone = "+15550001",
            address1 = "24 Pemberton Gardens",
            address2 = "Flat 2",
            city = "London",
            province = "London",
            zip = "N19 5RR",
            country = "United Kingdom",
            countryCode = "",
            provinceCode = "",
            formattedArea = "London, England, United Kingdom",
            latitude = 51.5678,
            longitude = -0.1212,
            isDefault = isDefault,
        )
    }
}
