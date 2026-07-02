package com.iti.pocketshop.features.auth.register.di

import com.iti.pocketshop.features.auth.register.data.datasource.firebase.RegistrationFirestoreDataSource
import com.iti.pocketshop.features.auth.register.data.datasource.firebase.RegistrationFirestoreDataSourceImpl
import com.iti.pocketshop.features.auth.register.data.datasource.shopify.ShopifyCustomerDataSource
import com.iti.pocketshop.features.auth.register.data.datasource.shopify.ShopifyCustomerDataSourceImpl
import com.iti.pocketshop.features.auth.register.data.repository.RegisterRepositoryImpl
import com.iti.pocketshop.features.auth.register.domain.repository.RegisterRepository
import com.iti.pocketshop.features.auth.register.domain.repository.SharedAuthRepository
import com.iti.pocketshop.features.auth.shared.SharedAuthRepositoryImpl
import com.iti.pocketshop.features.auth.shared.datasource.AuthFirebaseDataSource
import com.iti.pocketshop.features.auth.shared.datasource.AuthFirebaseDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RegisterDi {

    @Binds
    @Singleton
    abstract fun bindAuthFirebaseDataSource(
        impl: AuthFirebaseDataSourceImpl
    ): AuthFirebaseDataSource

    @Binds
    @Singleton
    abstract fun bindRegistrationFirestoreDataSource(
        impl: RegistrationFirestoreDataSourceImpl,
    ): RegistrationFirestoreDataSource

    @Binds
    @Singleton
    abstract fun bindShopifyCustomerDataSource(
        impl: ShopifyCustomerDataSourceImpl,
    ): ShopifyCustomerDataSource

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        impl: RegisterRepositoryImpl
    ): RegisterRepository

    @Binds
    @Singleton
    abstract fun bindSharedAuthRepository(
        impl: SharedAuthRepositoryImpl
    ): SharedAuthRepository
}
