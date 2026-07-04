package com.iti.pocketshop.features.cart.data.mapper

import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.shopify.fragment.CartFields

fun CartFields.toDomain(): ShopifyCart {
    return ShopifyCart(
        id = this.id,
        checkoutUrl = this.checkoutUrl.toString(),
        totalQuantity = this.totalQuantity,
        subtotalAmount = this.cost.subtotalAmount.amount as Double,
        subtotalCurrencyCode = this.cost.subtotalAmount.currencyCode.name,
        totalAmount = this.cost.totalAmount.amount as Double,
        totalCurrencyCode = this.cost.totalAmount.currencyCode.name,
        lines = this.lines.edges.mapNotNull { edge ->
            val node = edge.node
            val productVariant = node.merchandise.onProductVariant
            if (productVariant != null) {
                CartLineItem(
                    lineId = node.id,
                    variantId = productVariant.id,
                    productId = productVariant.product.id,
                    title = productVariant.product.title,
                    variantTitle = productVariant.title,
                    quantity = node.quantity,
                    price = productVariant.price.amount as Double,
                    currencyCode = productVariant.price.currencyCode.name,
                    imageUrl = productVariant.image?.url?.toString() ?: ""
                )
            } else {
                null
            }
        }
    )
}
