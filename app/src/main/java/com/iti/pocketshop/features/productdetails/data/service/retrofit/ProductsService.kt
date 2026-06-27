package com.iti.pocketshop.features.productdetails.data.service.retrofit

import com.iti.pocketshop.features.productdetails.data.model.ProductResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductsService {
    @GET("products/{id}.json")
    suspend fun getProductByID(
        @Path("id") productId: String,
    ): ProductResponseDto

}
