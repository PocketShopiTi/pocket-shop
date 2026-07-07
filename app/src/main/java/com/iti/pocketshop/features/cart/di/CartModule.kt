package com.iti.pocketshop.features.cart.di

import com.iti.pocketshop.features.cart.data.datasource.CartLocalDataSource
import com.iti.pocketshop.features.cart.data.datasource.CartLocalDataSourceImpl
import com.iti.pocketshop.features.cart.data.datasource.ShopifyCartDataSource
import com.iti.pocketshop.features.cart.data.datasource.ShopifyCartDataSourceImpl
import com.iti.pocketshop.features.cart.data.repository.CartRepositoryImpl
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindShopifyCartDataSource(
        impl: ShopifyCartDataSourceImpl
    ): ShopifyCartDataSource

    @Binds
    @Singleton
    abstract fun bindCartLocalDataSource(
        impl: CartLocalDataSourceImpl
    ): CartLocalDataSource
}
