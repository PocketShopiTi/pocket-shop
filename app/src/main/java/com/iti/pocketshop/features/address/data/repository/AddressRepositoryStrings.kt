package com.iti.pocketshop.features.address.data.repository

import android.content.Context
import com.iti.pocketshop.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface AddressRepositoryStrings {
    val missingCustomerAccessToken: String
    val customerAccountNotFound: String
    val missingSavedAddress: String
    val missingUpdatedAddress: String
    val missingDeletedAddress: String
    val missingDefaultUpdate: String
}

class AndroidAddressRepositoryStrings @Inject constructor(
    @ApplicationContext private val context: Context,
) : AddressRepositoryStrings {
    override val missingCustomerAccessToken: String
        get() = context.getString(R.string.address_error_customer_account_missing_token)

    override val customerAccountNotFound: String
        get() = context.getString(R.string.address_error_customer_account_not_found)

    override val missingSavedAddress: String
        get() = context.getString(R.string.address_error_customer_account_missing_saved_address)

    override val missingUpdatedAddress: String
        get() = context.getString(R.string.address_error_customer_account_missing_updated_address)

    override val missingDeletedAddress: String
        get() = context.getString(R.string.address_error_customer_account_missing_deleted_address)

    override val missingDefaultUpdate: String
        get() = context.getString(R.string.address_error_customer_account_missing_default_update)
}
