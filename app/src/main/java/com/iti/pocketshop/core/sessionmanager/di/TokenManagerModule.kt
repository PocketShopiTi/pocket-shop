package com.iti.pocketshop.core.sessionmanager.di

import com.iti.pocketshop.core.sessionmanager.data.datasource.customerremote.CustomerCredentialsDataSource
import com.iti.pocketshop.core.sessionmanager.data.datasource.customerremote.CustomerCredentialsDataSourceImpl
import com.iti.pocketshop.core.sessionmanager.data.datasource.customerremote.ShopifyTokenDataSource
import com.iti.pocketshop.core.sessionmanager.data.datasource.customerremote.ShopifyTokenDataSourceImpl
import com.iti.pocketshop.core.sessionmanager.data.datasource.local.CustomerTokenStore
import com.iti.pocketshop.core.sessionmanager.data.datasource.local.SecureCustomerTokenStore
import com.iti.pocketshop.core.sessionmanager.data.repository.CustomerAccessTokenRepositoryImpl
import com.iti.pocketshop.core.sessionmanager.domain.repository.CustomerAccessTokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenManagerModule {
    @Binds
    @Singleton
    abstract fun bindCustomerCredentialsDataSource(
        impl: CustomerCredentialsDataSourceImpl,
    ): CustomerCredentialsDataSource

    @Binds
    @Singleton
    abstract fun bindShopifyTokenDataSource(
        impl: ShopifyTokenDataSourceImpl,
    ): ShopifyTokenDataSource

    @Binds
    @Singleton
    abstract fun bindCustomerTokenStore(
        impl: SecureCustomerTokenStore,
    ): CustomerTokenStore

    @Binds
    @Singleton
    abstract fun bindCustomerAccessTokenRepository(
        impl: CustomerAccessTokenRepositoryImpl,
    ): CustomerAccessTokenRepository
}