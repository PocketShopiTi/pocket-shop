package com.iti.pocketshop.features.auth.register.data.repository

import android.util.Log
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.auth.register.data.datasource.firebase.RegistrationFirestoreDataSource
import com.iti.pocketshop.features.auth.register.data.datasource.shopify.ShopifyCustomerCreation
import com.iti.pocketshop.features.auth.register.data.datasource.shopify.ShopifyCustomerDataSource
import com.iti.pocketshop.features.auth.register.domain.model.AuthData
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser
import com.iti.pocketshop.features.auth.register.domain.repository.RegisterRepository
import com.iti.pocketshop.features.auth.shared.datasource.AuthFirebaseDataSource
import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import java.security.SecureRandom
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: AuthFirebaseDataSource,
    private val firestore: RegistrationFirestoreDataSource,
    private val shopify: ShopifyCustomerDataSource,
    private val cartRepository: CartRepository,
    private val customerAccessTokenRepository: CustomerAccessTokenRepository,
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
                    createShopifyCustomer(user)
            }
        }

    }

    override suspend fun createShopifyCustomer(
        user: AuthUser,
    ): PocketResult<Unit, PocketDataError> {
        if (user.email.isBlank()) {
            return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
        }
        val (firstName, lastName) = splitDisplayName(user.displayName)
        return createShopifyCustomer(
            user = user,
            firstName = firstName,
            lastName = lastName,
        )
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

                is ShopifyCustomerCreation.AlreadyExists ->
                    return PocketResult.Error(PocketDataError.Auth.EMAIL_ALREADY_IN_USE)
            }
        }
        
        // Ensure customer access token is ready for linking the cart
        val sessionResult = customerAccessTokenRepository.getValidToken()
        val accessToken = when (sessionResult) {
            is PocketResult.Error -> null
            is PocketResult.Success -> sessionResult.data.accessToken
        }

        // Create and link cart if access token is available
        var cartId: String? = null
        if (accessToken != null) {
            val cartResult = cartRepository.createCart()
            if (cartResult is PocketResult.Success) {
                cartId = cartResult.data
                cartRepository.linkBuyerIdentity(cartId, accessToken)
            }
        }

        return firestore.saveUser(
            uid = user.uid,
            email = user.email,
            firstName = firstName,
            lastName = lastName,
            customerId = customerId,
            password = password,
            cartId = cartId
        )
    }


    private fun generatePassword(): String {
        val passwordLength = 32
        val passwordCharacters =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#%"
        return buildString(passwordLength) {
            repeat(passwordLength) {
                append(passwordCharacters[secureRandom.nextInt(passwordCharacters.length)])
            }
        }
    }

    private fun splitDisplayName(displayName: String?): Pair<String, String> {
        val parts = displayName.orEmpty()
            .trim()
            .split(Regex("\\s+"), limit = 2)
            .filter(String::isNotBlank)
        return parts.getOrElse(0) { "" } to parts.getOrElse(1) { "" }
    }


    private val secureRandom = SecureRandom()
}

