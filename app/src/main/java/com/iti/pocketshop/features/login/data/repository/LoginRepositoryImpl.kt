package com.iti.pocketshop.features.login.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.iti.pocketshop.features.login.domain.repository.LoginRepository
import com.iti.pocketshop.features.login.domain.mapper.LoginResult
import com.iti.pocketshop.features.login.domain.mapper.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepository {

    override suspend fun loginWithEmail(email: String, password: String): LoginResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return LoginResult.Error("Login failed. Please try again.")
            LoginResult.Success(
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    name = firebaseUser.displayName,
                    avatarUrl = firebaseUser.photoUrl?.toString()
                )
            )
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            LoginResult.Error("Incorrect email or password.")
        } catch (e: FirebaseAuthInvalidUserException) {
            LoginResult.Error("No account found with this email.")
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "An unexpected error occurred.")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): LoginResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
                ?: return LoginResult.Error("Google Sign-In failed. Please try again.")
            LoginResult.Success(
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName,
                    avatarUrl = firebaseUser.photoUrl?.toString()
                )
            )
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "An unexpected error occurred.")
        }
    }

    override suspend fun continueAsGuest(): LoginResult {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            val firebaseUser = result.user
                ?: return LoginResult.Error("Guest login failed. Please try again.")
            LoginResult.Success(
                User(
                    id = firebaseUser.uid,
                    email = "",
                    name = "Guest",
                    avatarUrl = null
                )
            )
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "An unexpected error occurred.")
        }
    }
}
