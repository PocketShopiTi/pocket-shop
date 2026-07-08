package com.iti.pocketshop.features.checkout.data.mappers

import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.core.pricing.PriceFormatter
import com.iti.pocketshop.features.home.domain.models.Money
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.shopify.admin.PaidOrderCreateMutation
import com.iti.pocketshop.shopify.admin.type.CountryCode
import com.iti.pocketshop.shopify.admin.type.CurrencyCode
import com.iti.pocketshop.shopify.admin.type.MailingAddressInput
import com.iti.pocketshop.shopify.admin.type.MoneyBagInput
import com.iti.pocketshop.shopify.admin.type.MoneyInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateDiscountCodeInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateFinancialStatus
import com.iti.pocketshop.shopify.admin.type.OrderCreateFixedDiscountCodeAttributesInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateLineItemInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateOrderInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateOrderTransactionInput
import com.iti.pocketshop.shopify.admin.type.OrderTransactionKind
import com.iti.pocketshop.shopify.admin.type.OrderTransactionStatus
import kotlin.math.roundToLong

const val COD_GATEWAY_NAME = "Cash on Delivery (COD)"

data class PaymentConfirmation(
    val transactionId: String?,
    val gateway: String,
    val amount: Money,
    val isPaid: Boolean = true,
)

enum class OrderFinancialStatus {
    PAID, PENDING, PARTIALLY_PAID, REFUNDED, VOIDED, UNKNOWN;

    companion object {
        fun fromRaw(raw: String?): OrderFinancialStatus =
            entries.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}

data class Order(
    val id: String,
    val name: String,
    val financialStatus: OrderFinancialStatus,
    val totalPrice: Money,
)

fun PaidOrderCreateMutation.Order.toDomain(): Order =
    Order(
        id = id,
        name = name,
        financialStatus = OrderFinancialStatus.fromRaw(displayFinancialStatus?.rawValue),
        totalPrice = Money(
            amount = totalPriceSet.shopMoney.amount.toString().toDoubleOrNull() ?: 0.0,
            currencyCode = totalPriceSet.shopMoney.currencyCode.rawValue,
        ),
    )

fun toOrderInput(
    cart: ShopifyCart,
    shippingAddress: Address,
    customer: UserData,
    payment: PaymentConfirmation,
): OrderCreateOrderInput {
    val currency =
        CurrencyCode.entries.firstOrNull { payment.amount.currencyCode == it.rawValue }
            ?: CurrencyCode.EGP
    return OrderCreateOrderInput(
        currency = Optional.present(currency),
        email = Optional.present(customer.email),
        lineItems = Optional.present(cart.lines.map { it.toLineItemInput(currency) }),
        shippingAddress = Optional.present(shippingAddress.toMailingAddressInput()),
        transactions = Optional.present(listOf(payment.toTransactionInput())),
        financialStatus = Optional.present(
            if (payment.isPaid) OrderCreateFinancialStatus.PAID
            else OrderCreateFinancialStatus.PENDING
        ),

        discountCode = Optional.presentIfNotNull(cart.toDiscountCodeInput(currency)),
    )
}

private fun ShopifyCart.toDiscountCodeInput(currency: CurrencyCode): OrderCreateDiscountCodeInput? {
    val code = appliedDiscountCodes.firstOrNull() ?: return null
    val discountAmount = ((subtotalAmount.amount - totalAmount.amount) * 100).roundToLong() / 100.0
    if (discountAmount <= 0.0) return null
    val convertedDiscount = PriceFormatter.convert(
        amount = discountAmount,
        sourceCurrencyCode = totalAmount.currencyCode.rawValue,
        targetCurrencyCode = currency.rawValue,
    )
    return OrderCreateDiscountCodeInput(
        itemFixedDiscountCode = Optional.present(
            OrderCreateFixedDiscountCodeAttributesInput(
                code = code,
                amountSet = Optional.present(
                    MoneyBagInput(
                        shopMoney = MoneyInput(
                            amount = convertedDiscount.amount,
                            currencyCode = currency,
                        ),
                    )
                ),
            )
        ),
    )
}

fun CartLineItem.toLineItemInput(currency: CurrencyCode): OrderCreateLineItemInput {
    val convertedPrice = PriceFormatter.convert(
        amount = price,
        sourceCurrencyCode = currencyCode,
        targetCurrencyCode = currency.rawValue,
    )
    return OrderCreateLineItemInput(
        priceSet = Optional.present(
            MoneyBagInput(
                shopMoney = MoneyInput(
                    amount = convertedPrice.amount,
                    currencyCode = currency,
                ),
            )
        ),
        productId = Optional.present(productId),
        title = Optional.present(title),
        variantId = Optional.present(variantId),
        variantTitle = Optional.present(variantTitle),
        quantity = quantity,
    )
}

fun Address.toMailingAddressInput(): MailingAddressInput =
    MailingAddressInput(
        address1 = Optional.present(address1),
        address2 = Optional.presentIfNotNull(address2),
        city = Optional.present(city),
        provinceCode = Optional.presentIfNotNull(provinceCode),
        countryCode = Optional.present(CountryCode.entries.firstOrNull { countryCode == it.rawValue }
            ?: CountryCode.EG),
        zip = Optional.present(zip),
        firstName = Optional.present(firstName),
        lastName = Optional.present(lastName),
        phone = Optional.presentIfNotNull(phone),
    )

fun PaymentConfirmation.toTransactionInput(): OrderCreateOrderTransactionInput =
    OrderCreateOrderTransactionInput(
        kind = Optional.present(OrderTransactionKind.SALE),
        status = Optional.present(
            if (isPaid) OrderTransactionStatus.SUCCESS else OrderTransactionStatus.PENDING
        ),
        amountSet = MoneyBagInput(
            shopMoney = MoneyInput(
                amount = amount.amount,
                currencyCode = CurrencyCode.entries.firstOrNull { it.rawValue == amount.currencyCode }
                    ?: CurrencyCode.EGP,
            ),
        ),
        gateway = Optional.present(gateway),
    )
