package com.iti.pocketshop.core.exceptions

sealed class ShopifyExceptions(message: String, cause: Throwable? = null) :
    AppException(message, cause) {

    class ProductNotFound(id: String) : ShopifyExceptions("Product not found: $id")

    class GraphQlError(message: String) : ShopifyExceptions(message)

}
