package com.iti.pocketshop.features.cart.di

import com.iti.pocketshop.features.cart.data.datasource.CartDataSource
import com.iti.pocketshop.features.cart.data.datasource.CartDataSourceImpl
import com.iti.pocketshop.features.cart.data.repo.CartRepositoryImpl
import com.iti.pocketshop.features.cart.domain.repo.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartDi {

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindCartDataSource(
        cartDataSourceImpl: CartDataSourceImpl,
    ): CartDataSource
}
