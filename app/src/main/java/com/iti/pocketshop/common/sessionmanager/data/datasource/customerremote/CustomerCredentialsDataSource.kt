package com.iti.pocketshop.common.sessionmanager.data.datasource.customerremote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.pocketshop.common.sessionmanager.domain.model.CustomerCredentials
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.safeFirestoreCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CustomerCredentialsDataSource {
    suspend fun load(): PocketResult<CustomerCredentials, PocketDataError.Auth>
}

class CustomerCredentialsDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : CustomerCredentialsDataSource {

    override suspend fun load(): PocketResult<CustomerCredentials, PocketDataError.Auth> {
        val user = auth.currentUser
            ?: return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
        if (user.isAnonymous || user.email.isNullOrBlank()) {
            return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
        }

        return safeFirestoreCall {
            val snapshot = firestore.collection(USERS).document(user.uid).get().await()
            val password = snapshot.getString(SHOPIFY_PASSWORD)
                ?: error("Shopify customer password is missing")
            CustomerCredentials(
                uid = user.uid,
                email = user.email.orEmpty(),
                shopifyPassword = password,
            )
        }
    }

    private companion object {
        const val USERS = "users"
        const val SHOPIFY_PASSWORD = "shopifyPassword"
    }
}
