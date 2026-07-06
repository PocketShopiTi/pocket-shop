package com.iti.pocketshop.features.cart.data.mapper

import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.shopify.fragment.CartFields
import com.iti.pocketshop.shopify.type.CurrencyCode

fun CartFields.toDomain(): ShopifyCart {
    return ShopifyCart(
        id = this.id,
        totalQuantity = this.totalQuantity,
        subtotalAmount = this.cost.subtotalAmount.toDomain(),
        totalAmount = this.cost.totalAmount.toDomain(),
        lines = this.lines.edges.mapNotNull { edge ->
            val node = edge.node
            node.toDomain()
        },
        appliedDiscountCodes = this.discountCodes.filter { it.applicable }.map { it.code }
    )
}

fun CartFields.Node.toDomain(): CartLineItem? {
    val productVariant = merchandise.onProductVariant
    return productVariant?.let {
        CartLineItem(
            lineId = id,
            variantId = productVariant.id,
            productId = productVariant.product.id,
            title = productVariant.product.title,
            variantTitle = productVariant.title,
            quantity = quantity,
            price = productVariant.price.amount,
            currencyCode = productVariant.price.currencyCode.name,
            imageUrl = productVariant.image?.url ?: ""
        )
    }
}

data class TotalAmount(
    val amount: Double,
    val currencyCode: CurrencyCode
)

fun CartFields.TotalAmount.toDomain(): TotalAmount {
    return TotalAmount(
        amount = this.amount,
        currencyCode = this.currencyCode
    )
}

data class SubtotalAmount(
    val amount: Double,
    val currencyCode: CurrencyCode
)

fun CartFields.SubtotalAmount.toDomain(): SubtotalAmount {
    return SubtotalAmount(
        amount = this.amount,
        currencyCode = this.currencyCode
    )
}