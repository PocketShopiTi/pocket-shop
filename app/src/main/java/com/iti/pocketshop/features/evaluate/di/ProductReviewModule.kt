package com.iti.pocketshop.features.evaluate.di

import com.iti.pocketshop.features.evaluate.data.ProductReviewRepositoryImpl
import com.iti.pocketshop.features.evaluate.data.datasource.ProductReviewAdminDataSource
import com.iti.pocketshop.features.evaluate.data.datasource.ProductReviewAdminDataSourceImpl
import com.iti.pocketshop.features.evaluate.domain.Repository.ProductReviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductReviewModule {

    @Binds
    @Singleton
    abstract fun bindProductReviewAdminDataSource(
        impl: ProductReviewAdminDataSourceImpl,
    ): ProductReviewAdminDataSource

    @Binds
    @Singleton
    abstract fun bindProductReviewRepository(
        impl: ProductReviewRepositoryImpl,
    ): ProductReviewRepository
}
