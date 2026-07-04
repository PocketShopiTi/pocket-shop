package com.iti.pocketshop.features.cart.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.features.auth.register.data.datasource.firebase.RegistrationFirestoreDataSource
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class RestoreCartUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: RegistrationFirestoreDataSource,
    private val cartRepository: CartRepository,
    private val tokenRepository: CustomerAccessTokenRepository
) {
    suspend operator fun invoke(): PocketResult<ShopifyCart, PocketDataError> {
        val user = auth.currentUser
        if (user == null || user.isAnonymous) {
            cartRepository.clearLocalCart()
            return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
        }

        val uid = user.uid
        val cartIdResult = firestore.loadCartId(uid)
        var cartId: String? = null

        if (cartIdResult is PocketResult.Success) {
            cartId = cartIdResult.data
        }

        if (cartId != null) {
            // Attempt to load existing cart
            val loadResult = cartRepository.loadCart(cartId)
            if (loadResult is PocketResult.Success) {
                return loadResult
            }
        }

        // Either cartId is null (legacy user) or loading failed (expired/deleted cart)
        // Create a new cart
        val createResult = cartRepository.createCart()
        if (createResult is PocketResult.Error) {
            return createResult
        }
        val newCartId = (createResult as PocketResult.Success).data

        // Save new cartId to Firestore
        firestore.saveCartId(uid, newCartId)

        // Link buyer identity
        val sessionResult = tokenRepository.getValidToken()
        if (sessionResult is PocketResult.Success) {
            cartRepository.linkBuyerIdentity(newCartId, sessionResult.data.accessToken)
        }

        // Load the final cart state
        return cartRepository.loadCart(newCartId)
    }
}
