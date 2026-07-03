package com.iti.pocketshop.core.userdata

interface CustomerAccessTokenProvider {
    suspend fun currentCustomerAccessToken(): String?
}
