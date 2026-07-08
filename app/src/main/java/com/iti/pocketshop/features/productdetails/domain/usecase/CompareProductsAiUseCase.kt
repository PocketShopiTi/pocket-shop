package com.iti.pocketshop.features.productdetails.domain.usecase

import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.home.domain.models.Product
import com.iti.pocketshop.features.productdetails.domain.entity.AiComparisonResult
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.repository.AiCompareRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CompareProductsAiUseCase @Inject constructor(
    private val repository: AiCompareRepository,
    private val getProductDetails: GetProductDetailsUseCase,
) {
    suspend operator fun invoke(
        currentProduct: ProductDetails,
        selectedProducts: List<Product>
    ): Result<AiComparisonResult> = coroutineScope {

        val productsInfoDeferred = selectedProducts.map { product ->
            async {
                var desc = ""
                getProductDetails(product.id)
                    .onSuccess { details -> desc = details.description }
                    .onError { }
                val priceInfo = buildString {
                    append("${product.price.amount} ${product.price.currencyCode}")
                    if (product.compareAtPrice != null) {
                        append(" (was ${product.compareAtPrice.amount} ${product.compareAtPrice.currencyCode})")
                    }
                }
                val productSummary = """Price: $priceInfo
                 Brand/Vendor: ${product.vendor}
                   Available: ${if (product.availableForSale) "Yes" else "No"}
                Description: ${desc.ifBlank { "No description available" }}"""
                product.title to productSummary
            }
        }

        val selectedProductsInfo = productsInfoDeferred.awaitAll().toMap()

         val currentPrice = currentProduct.defaultVariant?.let { variant ->
            val base = "${variant.price.amount} ${variant.price.currencyCode}"
            if (variant.compareAtPrice != null) "$base (was ${variant.compareAtPrice.amount} ${variant.compareAtPrice.currencyCode})"
            else base
        } ?: "Price not available"

        repository.compareProducts(
            currentProductTitle = currentProduct.title,
            currentProductDesc = currentProduct.description,
            currentProductVendor = currentProduct.vendor,
            currentProductPrice = currentPrice,
            currentProductRating = currentProduct.rating,
            selectedProductsInfo = selectedProductsInfo
        )
    }
}