package com.iti.pocketshop.features.orders.di

import com.iti.pocketshop.features.orders.data.MockOrdersRepository
import com.iti.pocketshop.features.orders.data.datasource.OrdersRemoteDataSource
import com.iti.pocketshop.features.orders.data.datasource.OrdersRemoteDataSourceImpl
import com.iti.pocketshop.features.orders.domain.repository.OrdersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OrdersModule {

    @Binds
    abstract fun bindOrdersRepository(
        impl: MockOrdersRepository,
    ): OrdersRepository

    @Binds
    abstract fun bindOrdersRemoteDataSource(
        impl: OrdersRemoteDataSourceImpl,
    ): OrdersRemoteDataSource
}
