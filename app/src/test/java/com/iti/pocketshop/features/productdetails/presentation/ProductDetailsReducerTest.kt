package com.iti.pocketshop.features.productdetails.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductDetailsReducerTest {

    @Test
    fun `quantity never drops below one`() {
        val result = reduceProductDetails(
            state = ProductDetailsState(quantity = 1),
            action = ProductDetailsAction.DecreaseQuantity,
        )

        assertEquals(1, result.quantity)
    }

    @Test
    fun `selecting an option preserves the other selections`() {
        val product = ProductDetailsMockData.create("test-product")
        val initial = ProductDetailsState(
            product = product,
            selectedOptionValueIds = mapOf("colour" to "ecru", "size" to "s"),
        )

        val result = reduceProductDetails(
            state = initial,
            action = ProductDetailsAction.OptionSelected("size", "m"),
        )

        assertEquals("ecru", result.selectedOptionValueIds["colour"])
        assertEquals("m", result.selectedOptionValueIds["size"])
    }

    @Test
    fun `favorite and description toggles are independent`() {
        val favorite = reduceProductDetails(
            state = ProductDetailsState(),
            action = ProductDetailsAction.ToggleFavorite,
        )
        val expanded = reduceProductDetails(
            state = favorite,
            action = ProductDetailsAction.ToggleDescription,
        )

        assertTrue(expanded.isFavorite)
        assertTrue(expanded.isDescriptionExpanded)
        assertFalse(ProductDetailsState().isFavorite)
    }
}
