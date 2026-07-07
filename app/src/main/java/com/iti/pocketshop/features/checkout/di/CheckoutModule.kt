package com.iti.pocketshop.features.checkout.di

import com.iti.pocketshop.features.checkout.data.repository.CheckoutRepositoryImpl
import com.iti.pocketshop.features.checkout.domain.repository.CheckoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckoutModule {

    @Binds
    abstract fun bindCheckoutRepository(
        impl: CheckoutRepositoryImpl
    ): CheckoutRepository
}
