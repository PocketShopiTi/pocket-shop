package com.iti.pocketshop.features.payment.di

import com.iti.pocketshop.features.payment.data.remote.StripePaymentDataSource
import com.iti.pocketshop.features.payment.data.remote.StripePaymentDataSourceImpl
import com.iti.pocketshop.features.payment.data.repository.PaymentRepositoryImpl
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentHttpClient

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl,
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindStripePaymentDataSource(
        stripePaymentDataSourceImpl: StripePaymentDataSourceImpl,
    ): StripePaymentDataSource

    companion object {
        @Provides
        @Singleton
        @PaymentHttpClient
        fun providePaymentHttpClient(): HttpClient = HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json { ignoreUnknownKeys = true }
                )
            }
        }
    }
}
