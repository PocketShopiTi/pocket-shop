package com.iti.pocketshop.features.cart.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.features.auth.register.data.datasource.firebase.RegistrationFirestoreDataSource
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class RestoreOrCreateCartUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: RegistrationFirestoreDataSource,
    private val cartRepository: CartRepository,
    private val tokenRepository: CustomerAccessTokenRepository
) {
    suspend operator fun invoke(
        forceRefresh: Boolean = false
    ): PocketResult<ShopifyCart, PocketDataError> {
        val user = auth.currentUser
        if (user == null || user.isAnonymous) {
            cartRepository.clearLocalCart()
            return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
        }
        val uid = user.uid
        if (!forceRefresh) {
            val cartIdResult = firestore.loadCartId(uid)
            var cartId: String? = null
            if (cartIdResult is PocketResult.Success) {
                cartId = cartIdResult.data
            }
            if (cartId != null) {
                val loadResult = cartRepository.loadCart(cartId)
                if (loadResult is PocketResult.Success) {
                    return loadResult
                }
            }
        }
        val createResult = cartRepository.createCart()
        if (createResult is PocketResult.Error) {
            return createResult
        }
        val newCartId = (createResult as PocketResult.Success).data
        firestore.saveCartId(uid, newCartId)
        val sessionResult = tokenRepository.getValidToken()
        if (sessionResult is PocketResult.Success) {
            cartRepository.linkBuyerIdentity(newCartId, sessionResult.data.accessToken)
        }
        return cartRepository.loadCart(newCartId)
    }
}
