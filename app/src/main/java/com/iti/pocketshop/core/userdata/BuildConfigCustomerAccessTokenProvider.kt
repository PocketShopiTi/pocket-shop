package com.iti.pocketshop.core.userdata

import com.iti.pocketshop.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildConfigCustomerAccessTokenProvider @Inject constructor() : CustomerAccessTokenProvider {
    override fun currentCustomerAccessToken(): String? {
        return BuildConfig.CUSTOMER_ACCESS_TOKEN.takeIf { it.isNotBlank() }
    }
}
