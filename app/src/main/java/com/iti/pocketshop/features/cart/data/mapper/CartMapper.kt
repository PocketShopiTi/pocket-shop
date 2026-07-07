package com.iti.pocketshop.features.cart.data.mapper

import com.iti.pocketshop.features.cart.data.local.CartLineItemEntity
import com.iti.pocketshop.features.cart.data.local.ShopifyCartEntity
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
        appliedDiscountCodes = this.discountCodes.filter { it.applicable }.map { it.code },
    )
}

fun ShopifyCart.toEntity(): ShopifyCartEntity {
    return ShopifyCartEntity(
        id = this.id,
        subtotalAmount = this.subtotalAmount.amount,
        totalAmount = this.totalAmount.amount,
        totalQuantity = this.totalQuantity,
        currencyCode = this.totalAmount.currencyCode.name,
        appliedDiscountCodes = this.appliedDiscountCodes.filter(String::isNotBlank)
            .joinToString("^")
    )
}

fun ShopifyCartEntity.toDomain(lines: List<CartLineItem>): ShopifyCart {
    return ShopifyCart(
        id = this.id,
        subtotalAmount = SubtotalAmount(
            amount = this.subtotalAmount,
            currencyCode = CurrencyCode.entries.firstOrNull { it.name == this.currencyCode }
                ?: CurrencyCode.EGP
        ),
        totalAmount = TotalAmount(
            amount = this.totalAmount,
            currencyCode = CurrencyCode.entries.firstOrNull { it.name == this.currencyCode }
                ?: CurrencyCode.EGP
        ),
        totalQuantity = this.totalQuantity,
        appliedDiscountCodes = appliedDiscountCodes.takeIf { it.isNotBlank() }?.split("^")
            ?: emptyList(),
        lines = lines
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

fun CartLineItem.toEntity(): CartLineItemEntity {
    return CartLineItemEntity(
        lineId = lineId,
        variantId = variantId,
        productId = productId,
        title = title,
        variantTitle = variantTitle,
        quantity = quantity,
        price = price,
        currencyCode = currencyCode,
        imageUrl = imageUrl
    )
}

fun CartLineItemEntity.toDomain(): CartLineItem {
    return CartLineItem(
        lineId = lineId,
        variantId = variantId,
        productId = productId,
        title = title,
        variantTitle = variantTitle,
        quantity = quantity,
        price = price,
        currencyCode = currencyCode,
        imageUrl = imageUrl
    )
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