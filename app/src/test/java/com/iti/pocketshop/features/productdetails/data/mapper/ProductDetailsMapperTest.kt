package com.iti.pocketshop.features.productdetails.data.mapper

import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductDetailsMapperTest {

    @Test
    fun `exact default title option is synthetic`() {
        assertTrue(isDefaultOption("Title", listOf("Default Title")))
        assertTrue(isDefaultOption(" title ", listOf(" default title ")))
    }

    @Test
    fun `real title options are preserved`() {
        assertFalse(isDefaultOption("Title", listOf("Mr", "Ms")))
        assertFalse(isDefaultOption("Style", listOf("Default Title")))
    }

    @Test
    fun `option types use swatches and recognized names`() {
        assertEquals(ProductOptionType.COLOR, classifyOptionType("Material", hasSwatch = true))
        assertEquals(ProductOptionType.COLOR, classifyOptionType("Colour", hasSwatch = false))
        assertEquals(ProductOptionType.SIZE, classifyOptionType("Size", hasSwatch = false))
        assertEquals(ProductOptionType.GENERIC, classifyOptionType("Material", hasSwatch = false))
    }

    @Test
    fun `variant selection ids exclude options removed from the domain`() {
        val result = selectedOptionValueIds(
            selectedOptions = listOf("Title" to "Default Title", "Color" to "Blue"),
            optionIdsByName = mapOf("color" to "color-option"),
        )

        assertEquals(mapOf("color-option" to "color-option:blue"), result)
    }

    @Test
    fun `multi word colour names resolve to their base hue`() {
        assertEquals(colourArgb("White"), colourArgb("Cloud White"))
        assertEquals(colourArgb("Black"), colourArgb("Core Black"))
        assertEquals(colourArgb("Red"), colourArgb("Solar Red"))
        assertEquals(colourArgb("black"), colourArgb(" CORE BLACK "))
    }

    @Test
    fun `unknown colour names share a single fallback`() {
        assertEquals(colourArgb("Zebra"), colourArgb("Mystery"))
    }
}
