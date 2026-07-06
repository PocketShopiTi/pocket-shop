package com.iti.pocketshop.features.address.data.datasource

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.shopify.CreateCustomerAddressMutation
import com.iti.pocketshop.shopify.DeleteCustomerAddressMutation
import com.iti.pocketshop.shopify.GetCustomerAddressesQuery
import com.iti.pocketshop.shopify.SetDefaultCustomerAddressMutation
import com.iti.pocketshop.shopify.UpdateCustomerAddressMutation
import com.iti.pocketshop.shopify.type.MailingAddressInput

interface AddressRemoteDataSource {
    suspend fun getAddresses(
        customerAccessToken: String,
    ): PocketResult<GetCustomerAddressesQuery.Data, PocketDataError.Remote>

    suspend fun createAddress(
        customerAccessToken: String,
        address: MailingAddressInput,
    ): PocketResult<CreateCustomerAddressMutation.Data, PocketDataError.Remote>

    suspend fun updateAddress(
        customerAccessToken: String,
        addressId: String,
        address: MailingAddressInput,
    ): PocketResult<UpdateCustomerAddressMutation.Data, PocketDataError.Remote>

    suspend fun deleteAddress(
        customerAccessToken: String,
        addressId: String,
    ): PocketResult<DeleteCustomerAddressMutation.Data, PocketDataError.Remote>

    suspend fun setDefaultAddress(
        customerAccessToken: String,
        addressId: String,
    ): PocketResult<SetDefaultCustomerAddressMutation.Data, PocketDataError.Remote>
}
