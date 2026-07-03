package com.iti.pocketshop.features.address.data.datasource

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.CreateCustomerAddressMutation
import com.iti.pocketshop.shopify.DeleteCustomerAddressMutation
import com.iti.pocketshop.shopify.GetCustomerAddressesQuery
import com.iti.pocketshop.shopify.SetDefaultCustomerAddressMutation
import com.iti.pocketshop.shopify.UpdateCustomerAddressMutation
import com.iti.pocketshop.shopify.type.MailingAddressInput
import javax.inject.Inject

class AddressRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : AddressRemoteDataSource {
    override suspend fun getAddresses(
        customerAccessToken: String,
    ): PocketResult<GetCustomerAddressesQuery.Data, PocketDataError.Remote> {
        return apolloClient
            .query(
                GetCustomerAddressesQuery(
                    customerAccessToken = customerAccessToken,
                ),
            )
            .safeCall()
    }

    override suspend fun createAddress(
        customerAccessToken: String,
        address: MailingAddressInput,
    ): PocketResult<CreateCustomerAddressMutation.Data, PocketDataError.Remote> {
        return apolloClient
            .mutation(
                CreateCustomerAddressMutation(
                    customerAccessToken = customerAccessToken,
                    address = address,
                ),
            )
            .safeCall()
    }

    override suspend fun updateAddress(
        customerAccessToken: String,
        addressId: String,
        address: MailingAddressInput,
    ): PocketResult<UpdateCustomerAddressMutation.Data, PocketDataError.Remote> {
        return apolloClient
            .mutation(
                UpdateCustomerAddressMutation(
                    customerAccessToken = customerAccessToken,
                    id = addressId,
                    address = address,
                ),
            )
            .safeCall()
    }

    override suspend fun deleteAddress(
        customerAccessToken: String,
        addressId: String,
    ): PocketResult<DeleteCustomerAddressMutation.Data, PocketDataError.Remote> {
        return apolloClient
            .mutation(
                DeleteCustomerAddressMutation(
                    customerAccessToken = customerAccessToken,
                    id = addressId,
                ),
            )
            .safeCall()
    }

    override suspend fun setDefaultAddress(
        customerAccessToken: String,
        addressId: String,
    ): PocketResult<SetDefaultCustomerAddressMutation.Data, PocketDataError.Remote> {
        return apolloClient
            .mutation(
                SetDefaultCustomerAddressMutation(
                    customerAccessToken = customerAccessToken,
                    addressId = addressId,
                ),
            )
            .safeCall()
    }
}
