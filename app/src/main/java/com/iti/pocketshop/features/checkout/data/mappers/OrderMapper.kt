package com.iti.pocketshop.features.checkout.data.mappers

import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.home.domain.models.Money
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.shopify.admin.PaidOrderCreateMutation
import com.iti.pocketshop.shopify.admin.type.CountryCode
import com.iti.pocketshop.shopify.admin.type.CurrencyCode
import com.iti.pocketshop.shopify.admin.type.MailingAddressInput
import com.iti.pocketshop.shopify.admin.type.MoneyBagInput
import com.iti.pocketshop.shopify.admin.type.MoneyInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateLineItemInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateOrderInput
import com.iti.pocketshop.shopify.admin.type.OrderCreateOrderTransactionInput
import com.iti.pocketshop.shopify.admin.type.OrderTransactionKind
import com.iti.pocketshop.shopify.admin.type.OrderTransactionStatus


data class PaymentConfirmation(
    val transactionId: String,
    val gateway: String,
    val amount: Money,
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
    return OrderCreateOrderInput(
        currency = Optional.present(CurrencyCode.entries.firstOrNull { cart.totalAmount.currencyCode.rawValue == it.rawValue }
            ?: CurrencyCode.EGP),
        email = Optional.present(customer.email),
        lineItems = Optional.present(cart.lines.map { it.toLineItemInput() }),
        shippingAddress = Optional.present(shippingAddress.toMailingAddressInput()),
        transactions = Optional.present(listOf(payment.toTransactionInput())),
    )
}

fun CartLineItem.toLineItemInput(): OrderCreateLineItemInput =
    OrderCreateLineItemInput(
        productId = Optional.present(productId),
        title = Optional.present(title),
        variantId = Optional.present(variantId),
        variantTitle = Optional.present(variantTitle),
        quantity = quantity,
    )

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
        status = Optional.present(OrderTransactionStatus.SUCCESS),
        amountSet = MoneyBagInput(
            shopMoney = MoneyInput(
                amount = amount.amount,
                currencyCode = CurrencyCode.entries.firstOrNull { it.rawValue == amount.currencyCode }
                    ?: CurrencyCode.EGP,
            ),
        ),
        gateway = Optional.present(gateway),
    )
