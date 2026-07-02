package com.iti.pocketshop.features.auth.register.data.repository

import android.util.Log
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.data.datasource.firebase.RegistrationFirestoreDataSource
import com.iti.pocketshop.features.auth.register.data.datasource.shopify.ShopifyCustomerCreation
import com.iti.pocketshop.features.auth.register.data.datasource.shopify.ShopifyCustomerDataSource
import com.iti.pocketshop.features.auth.register.domain.model.AuthData
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser
import com.iti.pocketshop.features.auth.register.domain.repository.RegisterRepository
import com.iti.pocketshop.features.auth.shared.datasource.AuthFirebaseDataSource
import java.security.SecureRandom
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: AuthFirebaseDataSource,
    private val firestore: RegistrationFirestoreDataSource,
    private val shopify: ShopifyCustomerDataSource,
) : RegisterRepository {
    private val TAG = "RegisterRepositoryImpl"
    override suspend fun register(data: AuthData): PocketResult<Unit, PocketDataError> {
        val current = firebaseAuth.currentUser()

        Log.i(TAG, "register: firebaseAuth.currentUser() = $current")
        val user = if (
            current != null &&
            !current.isAnonymous &&
            !current.isEmailVerified &&
            current.email.equals(data.email, ignoreCase = true)
        ) {
            current
        } else {
            val result = firebaseAuth.createUser(data)
            Log.i(TAG, "register: firebaseAuth.createUser() = $result")
            when (result) {
                is PocketResult.Error -> return result
                is PocketResult.Success -> result.data
            }
        }

        val createdCustomer = createShopifyCustomer(
            user = user,
            firstName = data.firstName,
            lastName = data.lastName,
        )
        Log.i(TAG, "register: createShopifyCustomer = $createdCustomer")

        when (createdCustomer) {
            is PocketResult.Error -> return createdCustomer
            is PocketResult.Success -> Unit
        }

        firebaseAuth.sendVerificationEmail()
        return PocketResult.Success(Unit)
    }

    override suspend fun ensureShopifyCustomer(
        user: AuthUser,
    ): PocketResult<Unit, PocketDataError> {

        return when (val result = firestore.load(user.uid)) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                if (result.data?.shopifyCustomerId != null)
                    PocketResult.Success(Unit)
                else
                    PocketResult.Error(PocketDataError.Auth.UnAuthorized)
            }
        }

    }

    private suspend fun createShopifyCustomer(
        user: AuthUser,
        firstName: String,
        lastName: String,
    ): PocketResult<Unit, PocketDataError> {

        val password = generatePassword()

        val createdCustomerResult = shopify.create(
            email = user.email,
            password = password,
            firstName = firstName,
            lastName = lastName,
        )

        val customerId = when (createdCustomerResult) {
            is PocketResult.Error -> return createdCustomerResult

            is PocketResult.Success -> when (val customer = createdCustomerResult.data) {
                is ShopifyCustomerCreation.Created -> customer.customerId

                is ShopifyCustomerCreation.AlreadyExists -> {
                    null
                }
            }
        }

        return firestore.saveUser(
            uid = user.uid,
            email = user.email,
            firstName = firstName,
            lastName = lastName,
            customerId = customerId,
            password = password
        )
    }


    fun generatePassword(): String {
        val PASSWORD_LENGTH = 32
        val CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#%"

        return buildString(PASSWORD_LENGTH) {
            repeat(PASSWORD_LENGTH) {
                append(CHARACTERS[SecureRandom().nextInt(CHARACTERS.length)])
            }
        }
    }


}

