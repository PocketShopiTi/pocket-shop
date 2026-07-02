package com.iti.pocketshop.features.auth.register.data.datasource.shopify

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.CustomerCreateMutation
import com.iti.pocketshop.shopify.type.CustomerCreateInput
import javax.inject.Inject

class ShopifyCustomerDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : ShopifyCustomerDataSource {

    override suspend fun create(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
    ): PocketResult<ShopifyCustomerCreation, PocketDataError> {

        val result = apolloClient.mutation(
            CustomerCreateMutation(
                CustomerCreateInput(
                    firstName = Optional.Present(firstName),
                    lastName = Optional.Present(lastName),
                    email = email,
                    password = password,
                )
            )
        ).safeCall()

        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {

                val payload = result.data.customerCreate
                    ?: return PocketResult.Error(PocketDataError.Auth.UNKNOWN)

                val customerId = payload.customer?.id
                val errorCode = payload.customerUserErrors.firstOrNull()?.code?.rawValue

                when {
                    customerId != null -> PocketResult.Success(
                        ShopifyCustomerCreation.Created(customerId)
                    )

                    errorCode == "TAKEN" -> PocketResult.Success(ShopifyCustomerCreation.AlreadyExists)
                    else -> PocketResult.Error(errorCode.toAuthError())
                }
            }
        }
    }

    private fun String?.toAuthError(): PocketDataError.Auth =
        when (this) {
            "TAKEN" -> PocketDataError.Auth.EMAIL_ALREADY_IN_USE
            "CUSTOMER_DISABLED" -> PocketDataError.Auth.USER_DISABLED
            else -> PocketDataError.Auth.UNKNOWN
        }
}
