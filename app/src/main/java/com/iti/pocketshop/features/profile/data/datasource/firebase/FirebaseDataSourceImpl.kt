package com.iti.pocketshop.features.profile.data.datasource.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeFirebaseCall
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : FirebaseDataSource {

    override suspend fun getUserSession(): PocketResult<FirebaseUser?, PocketDataError.Auth> =
        safeFirebaseCall {
            return@safeFirebaseCall firebaseAuth.currentUser
        }

    override suspend fun logout(): PocketResult<Unit, PocketDataError.Auth> =
        safeFirebaseCall { firebaseAuth.signOut() }
}