package com.iti.pocketshop.features.auth.register.data.datasource.shopify

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult

interface ShopifyCustomerDataSource {
    suspend fun create(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
    ): PocketResult<ShopifyCustomerCreation, PocketDataError>
}

sealed interface ShopifyCustomerCreation {
    data class Created(val customerId: String) : ShopifyCustomerCreation
    data object AlreadyExists : ShopifyCustomerCreation
}
