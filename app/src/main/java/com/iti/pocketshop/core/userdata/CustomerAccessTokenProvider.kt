package com.iti.pocketshop.core.userdata

interface CustomerAccessTokenProvider {
    fun currentCustomerAccessToken(): String?
}
