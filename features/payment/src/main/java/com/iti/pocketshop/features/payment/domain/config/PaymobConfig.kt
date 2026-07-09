package com.iti.pocketshop.features.payment.domain.config

interface PaymobConfig {
    val secretKey: String
    val publicKey: String
    val cardIntegrationId: Int
    val walletIntegrationId: Int
    val kioskIntegrationId: Int
}
