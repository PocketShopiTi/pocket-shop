package com.iti.pocketshop.features.orders.data

import com.iti.pocketshop.common.sessionmanager.domain.usecase.GetAccessTokenUseCase
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.orders.data.datasource.OrdersRemoteDataSource
import com.iti.pocketshop.features.orders.data.mapper.toDetailsDomain
import com.iti.pocketshop.features.orders.data.mapper.toDomain
import com.iti.pocketshop.features.orders.domain.model.OrderDetails
import com.iti.pocketshop.features.orders.domain.model.OrdersPage
import com.iti.pocketshop.features.orders.domain.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val accessTokenUseCase: GetAccessTokenUseCase,
    private val ordersRemoteDataSource: OrdersRemoteDataSource,
) : OrdersRepository {

    override suspend fun getOrdersPage(
        pageSize: Int,
        after: String?,
    ): PocketResult<OrdersPage, PocketDataError> {
        return when (val tokenResult = accessTokenUseCase()) {
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

    override suspend fun getOrderDetails(
        orderId: String
    ): PocketResult<OrderDetails, PocketDataError> {
        val orderResult = ordersRemoteDataSource.getOrderDetails(
            orderId = orderId,
        )
        return when (orderResult) {
            is PocketResult.Error -> PocketResult.Error(orderResult.error)

            is PocketResult.Success -> {
                val order = orderResult.data
                    ?: return PocketResult.Error(PocketDataError.Remote.EMPTY_RESULT)
                PocketResult.Success(order.toDetailsDomain())
            }
        }
    }
}
