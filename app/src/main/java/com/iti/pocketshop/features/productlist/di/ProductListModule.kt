package com.iti.pocketshop.features.productlist.di

import com.iti.pocketshop.features.productlist.data.ProductListRemoteSourceImpl
import com.iti.pocketshop.features.productlist.data.ProductListRepositoryImpl
import com.iti.pocketshop.features.productlist.domain.ProductListRemoteSource
import com.iti.pocketshop.features.productlist.domain.ProductListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductListModule {

    @Binds
    abstract fun bindProductListRemoteSource(
        impl: ProductListRemoteSourceImpl,
    ): ProductListRemoteSource

    @Binds
    abstract fun bindProductListRepository(
        impl: ProductListRepositoryImpl,
    ): ProductListRepository
}
