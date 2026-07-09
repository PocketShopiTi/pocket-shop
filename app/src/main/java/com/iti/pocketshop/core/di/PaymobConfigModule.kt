package com.iti.pocketshop.core.di

import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.features.payment.domain.config.PaymobConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PaymobConfigModule {

    @Provides
    fun providePaymobConfig(): PaymobConfig = object : PaymobConfig {
        override val secretKey: String = BuildConfig.PAYMOB_SECRET_KEY
        override val publicKey: String = BuildConfig.PAYMOB_PUBLIC_KEY
        override val cardIntegrationId: Int = BuildConfig.CARD_PAYMOB_INTEGRATION_ID.toInt()
        override val walletIntegrationId: Int = BuildConfig.WALLET_PAYMOB_INTEGRATION_ID.toInt()
        override val kioskIntegrationId: Int = BuildConfig.KIOSK_PAYMOB_INTEGRATION_ID.toInt()
    }
}
