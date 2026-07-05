package com.iti.pocketshop.features.checkout.domain.repository

import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.domain.model.CheckoutAddress
import com.iti.pocketshop.features.checkout.domain.model.PaymentMethod

interface CheckoutRepository {
    suspend fun getCustomerAddresses(): Result<List<CheckoutAddress>>
    
    suspend fun applyCoupon(cartId: String, couponCode: String): Result<ShopifyCart>
    
    suspend fun removeCoupon(cartId: String): Result<ShopifyCart>
    
    suspend fun setDeliveryAddress(cartId: String, addressId: String): Result<ShopifyCart>
    
    suspend fun getCheckoutUrl(cartId: String): Result<String>
    
    suspend fun createTestAddress(): Result<Unit>
}
