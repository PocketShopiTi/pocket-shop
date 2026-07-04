package com.iti.pocketshop.features.productdetails.data.di

import com.iti.pocketshop.features.productdetails.data.datasource.ProductDetailsDataSource
import com.iti.pocketshop.features.productdetails.data.datasource.ShopifyProductDetailsDataSource
import com.iti.pocketshop.features.productdetails.data.repository.ProductDetailsRepositoryImpl
import com.iti.pocketshop.features.productdetails.domain.repository.ProductDetailsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDetailsDataModule {

    @Binds
    @Singleton
    abstract fun bindProductDetailsRemoteDataSource(
        implementation: ShopifyProductDetailsDataSource,
    ): ProductDetailsDataSource

    @Binds
    @Singleton
    abstract fun bindProductDetailsRepository(
        implementation: ProductDetailsRepositoryImpl,
    ): ProductDetailsRepository
}
