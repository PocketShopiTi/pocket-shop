package com.iti.pocketshop.core.di

import com.iti.pocketshop.core.userdata.BuildConfigCustomerAccessTokenProvider
import com.iti.pocketshop.core.userdata.CustomerAccessTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CustomerAccessTokenModule {

    @Binds
    @Singleton
    abstract fun bindCustomerAccessTokenProvider(
        impl: BuildConfigCustomerAccessTokenProvider,
    ): CustomerAccessTokenProvider
}