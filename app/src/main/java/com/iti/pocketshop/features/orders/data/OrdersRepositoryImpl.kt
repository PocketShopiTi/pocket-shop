package com.iti.pocketshop.features.orders.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.orders.data.datasource.OrdersRemoteDataSource
import com.iti.pocketshop.features.orders.data.mapper.toDomain
import com.iti.pocketshop.features.orders.domain.model.OrdersPage
import com.iti.pocketshop.features.orders.domain.repository.OrdersRepository
import com.iti.pocketshop.features.profile.data.datasource.shopify.ShopifyDataSource
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val shopifyDataSource: ShopifyDataSource,
    private val ordersRemoteDataSource: OrdersRemoteDataSource,
) : OrdersRepository {

    override suspend fun getOrdersPage(
        pageSize: Int,
        after: String?,
    ): PocketResult<OrdersPage, PocketDataError> {
        return when (val tokenResult = shopifyDataSource.getCustomerAccessToken()) {
            is PocketResult.Error -> PocketResult.Error(tokenResult.error)


            is PocketResult.Success -> {
                val ordersResult = ordersRemoteDataSource.getOrders(
                    accessToken = tokenResult.data,
                    pageSize = pageSize,
                    after = after,
                )

                when (
                    ordersResult
                ) {

                    is PocketResult.Error -> PocketResult.Error(ordersResult.error)

                    is PocketResult.Success -> {
                        val customer = ordersResult.data
                            ?: return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
                        PocketResult.Success(customer.orders.toDomain())
                    }
                }
            }
        }
    }
}
