package com.iti.pocketshop.core.userdata

import com.iti.pocketshop.core.tokenmanager.data.CustomerAccessTokenRepositoryImpl
import com.iti.pocketshop.core.tokenmanager.data.datasource.local.CustomerTokenStore
import com.iti.pocketshop.core.tokenmanager.data.datasource.local.SecureCustomerTokenStore
import com.iti.pocketshop.core.tokenmanager.data.datasource.remote.CustomerCredentialsDataSource
import com.iti.pocketshop.core.tokenmanager.data.datasource.remote.CustomerCredentialsDataSourceImpl
import com.iti.pocketshop.core.tokenmanager.data.datasource.remote.ShopifyTokenDataSource
import com.iti.pocketshop.core.tokenmanager.data.datasource.remote.ShopifyTokenDataSourceImpl
import com.iti.pocketshop.core.tokenmanager.domain.CustomerAccessTokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    @Binds
    @Singleton
    abstract fun bindUserRepo(impl: UserRepoImpl): UserRepo

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
