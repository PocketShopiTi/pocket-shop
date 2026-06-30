package com.iti.pocketshop.features.login.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.iti.pocketshop.features.login.domain.repository.LoginRepository
import com.iti.pocketshop.features.login.domain.model.LoginResult
import com.iti.pocketshop.features.login.domain.model.LoginError
import com.iti.pocketshop.features.login.domain.model.User
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepository {

    override suspend fun loginWithEmail(email: String, password: String): LoginResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return LoginResult.Error(LoginError.UNKNOWN)
            LoginResult.Success(
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    name = firebaseUser.displayName,
                    avatarUrl = firebaseUser.photoUrl?.toString()
                )
            )
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            LoginResult.Error(LoginError.WRONG_PASSWORD)
        } catch (e: FirebaseAuthInvalidUserException) {
            LoginResult.Error(LoginError.USER_NOT_FOUND)
        } catch (e: FirebaseNetworkException) {
            LoginResult.Error(LoginError.NETWORK_ERROR)
        } catch (e: Exception) {
            LoginResult.Error(LoginError.UNKNOWN)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): LoginResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
                ?: return LoginResult.Error(LoginError.GOOGLE_SIGN_IN_FAILED)
            LoginResult.Success(
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName,
                    avatarUrl = firebaseUser.photoUrl?.toString()
                )
            )
        } catch (e: FirebaseNetworkException) {
            LoginResult.Error(LoginError.NETWORK_ERROR)
        } catch (e: Exception) {
            LoginResult.Error(LoginError.UNKNOWN)
        }
    }

    override suspend fun continueAsGuest(): LoginResult {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            val firebaseUser = result.user
                ?: return LoginResult.Error(LoginError.UNKNOWN)
            LoginResult.Success(
                User(
                    id = firebaseUser.uid,
                    email = "",
                    name = "Guest",
                    avatarUrl = null
                )
            )
        } catch (e: FirebaseNetworkException) {
            LoginResult.Error(LoginError.NETWORK_ERROR)
        } catch (e: Exception) {
            LoginResult.Error(LoginError.UNKNOWN)
        }
    }
}
