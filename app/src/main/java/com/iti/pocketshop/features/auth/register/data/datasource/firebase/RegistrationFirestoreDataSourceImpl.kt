package com.iti.pocketshop.features.auth.register.data.datasource.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeFirestoreCall
import com.iti.pocketshop.features.auth.register.domain.model.RegistrationRecord
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegistrationFirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : RegistrationFirestoreDataSource {

    private companion object {
        const val USERS = "users"
        const val EMAIL = "email"
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val SHOPIFY_PASSWORD = "shopifyPassword"
        const val SHOPIFY_CUSTOMER_ID = "shopifyCustomerId"
    }

    override suspend fun load(
        uid: String,
    ): PocketResult<RegistrationRecord?, PocketDataError.Auth> =
        safeFirestoreCall {
            val snapshot = firestore.collection(USERS)
                .document(uid)
                .get()
                .await()
            val password = snapshot.getString(SHOPIFY_PASSWORD) ?: return@safeFirestoreCall null

            RegistrationRecord(
                shopifyPassword = password,
                shopifyCustomerId = snapshot.getString(SHOPIFY_CUSTOMER_ID),
            )
        }

    override suspend fun saveUser(
        uid: String,
        email: String,
        firstName: String,
        lastName: String,
        customerId: String?,
        password: String,
    ): PocketResult<Unit, PocketDataError.Auth> = safeFirestoreCall {
        val data = mutableMapOf<String, Any?>(
            EMAIL to email,
            FIRST_NAME to firstName,
            LAST_NAME to lastName,
            SHOPIFY_PASSWORD to password,
        )

        if (customerId != null) data[SHOPIFY_CUSTOMER_ID] = customerId

        firestore.collection(USERS)
            .document(uid)
            .set(data, SetOptions.merge())
            .await()
    }.map { }

}
