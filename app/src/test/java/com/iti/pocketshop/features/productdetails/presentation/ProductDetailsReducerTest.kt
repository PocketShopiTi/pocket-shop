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
    fun `quantity never exceeds ninety nine`() {
        val result = reduceProductDetails(
            state = ProductDetailsState(quantity = 99),
            action = ProductDetailsAction.IncreaseQuantity,
        )

        assertEquals(99, result.quantity)
    }

    @Test
    fun `total price follows selected variant and quantity`() {
        val product = ProductDetailsMockData.create("test-product")
        val state = ProductDetailsState(
            product = product,
            selectedOptionValueIds = mapOf("colour" to "ecru", "size" to "m"),
            quantity = 3,
        )

        assertEquals(987.0, state.totalPrice?.amount ?: 0.0, 0.0)
        assertEquals("USD", state.totalPrice?.currencyCode)
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

    @Test
    fun `available product shows and clears cart feedback`() {
        val product = ProductDetailsMockData.create("test-product")
        val state = ProductDetailsState(
            product = product,
            selectedOptionValueIds = mapOf("colour" to "ecru", "size" to "m"),
        )

        val added = reduceProductDetails(state, ProductDetailsAction.AddToCartClicked)
        val cleared = reduceProductDetails(added, ProductDetailsAction.CartFeedbackFinished)

        assertTrue(added.isAddedToCart)
        assertFalse(cleared.isAddedToCart)
    }
}
