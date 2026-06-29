package com.iti.pocketshop.features.login.presentation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.iti.pocketshop.R
import kotlinx.coroutines.launch


fun ComponentActivity.launchGoogleSignIn(
    onTokenReceived: (String) -> Unit,
    onError: () -> Unit
) {
    lifecycleScope.launch {
        try {
            val credentialManager = CredentialManager.create(this@launchGoogleSignIn)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(this@launchGoogleSignIn, request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                onTokenReceived(googleIdTokenCredential.idToken)
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign-in failed", e)
            onError()
        }
    }
}