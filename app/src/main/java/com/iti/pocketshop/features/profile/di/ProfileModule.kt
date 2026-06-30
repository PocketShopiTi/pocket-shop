package com.iti.pocketshop.features.profile.di

import com.iti.pocketshop.features.profile.data.ProfileRepositoryImpl
import com.iti.pocketshop.features.profile.data.datasource.firebase.FirebaseDataSource
import com.iti.pocketshop.features.profile.data.datasource.firebase.FirebaseDataSourceImpl
import com.iti.pocketshop.features.profile.data.datasource.shopify.ShopifyDataSource
import com.iti.pocketshop.features.profile.data.datasource.shopify.ShopifyDataSourceImpl
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    abstract fun bindProfileFirebaseDataSource(
        impl: FirebaseDataSourceImpl
    ): FirebaseDataSource

    @Binds
    abstract fun bindProfileShopifyDataSource(
        impl: ShopifyDataSourceImpl
    ): ShopifyDataSource

}