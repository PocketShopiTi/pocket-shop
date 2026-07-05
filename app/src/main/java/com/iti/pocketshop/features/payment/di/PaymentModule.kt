package com.iti.pocketshop.features.payment.di

import com.iti.pocketshop.features.payment.data.remote.PaymobPaymentDataSourceImpl
import com.iti.pocketshop.features.payment.data.repository.PaymentRepositoryImpl
import com.iti.pocketshop.features.payment.domain.datasource.PaymobPaymentDataSource
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {

    @Binds
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl,
    ): PaymentRepository

    @Binds
    abstract fun bindPaymobPaymentDataSource(
        paymobPaymentDataSourceImpl: PaymobPaymentDataSourceImpl,
    ): PaymobPaymentDataSource

}
