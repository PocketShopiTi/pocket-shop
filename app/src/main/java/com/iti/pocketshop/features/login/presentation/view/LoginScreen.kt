package com.iti.pocketshop.features.login.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import android.util.Log
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import com.iti.pocketshop.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.features.login.data.state.LoginState
import com.iti.pocketshop.features.login.presentation.action.LoginAction
import com.iti.pocketshop.features.login.presentation.view.component.AuthTextField
import com.iti.pocketshop.features.login.presentation.view.component.DividerWithText
import com.iti.pocketshop.features.login.presentation.view.component.ForgotPasswordLink
import com.iti.pocketshop.features.login.presentation.view.component.LoginActionButton
import com.iti.pocketshop.features.login.presentation.view.component.LoginFooter
import com.iti.pocketshop.features.login.presentation.view.component.LoginHeader
import com.iti.pocketshop.features.login.presentation.view.component.PasswordField
import com.iti.pocketshop.features.login.presentation.view.component.SocialSignInButton
import com.iti.pocketshop.features.login.presentation.viewmodel.LoginViewModel

@Composable
fun LoginRoot(
    openHome: () -> Unit,
    openOTP: () -> Unit,
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            openHome()
        }
    }

    LoginScreen(
        openOTP = openOTP,
        openRegister = openRegister,
        openForgotPassword = openForgotPassword,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun LoginScreen(
    openOTP: () -> Unit,
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val webClientId = stringResource(id = R.string.default_web_client_id)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .imePadding()
        ) {
             Text(
                text = "Continue as guest",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .clickable { onAction(LoginAction.ContinueAsGuestClicked) }
            )

             Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginHeader()

                AuthTextField(
                    label = "Email",
                    value = state.email,
                    onValueChange = { onAction(LoginAction.EmailChanged(it)) },
                    placeholder = "sofia@example.com",
                    modifier = Modifier.padding(top = 24.dp),
                    isError = state.emailError != null || state.generalError != null,
                    errorMessage = state.emailError,
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) }
                )

                PasswordField(
                    value = state.password,
                    onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
                    modifier = Modifier.padding(top = 16.dp),
                    isError = state.passwordError != null || state.generalError != null,
                    errorMessage = state.passwordError ?: state.generalError
                )

                ForgotPasswordLink(
                    onClick = openForgotPassword,
                    modifier = Modifier.padding(top = 8.dp)
                )

                LoginActionButton(
                    text = "Log in",
                    onClick = { onAction(LoginAction.LoginClicked) },
                    enabled = !state.isLoading,
                    modifier = Modifier.padding(top = 24.dp)
                )

                DividerWithText(
                    text = "or continue with",
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                SocialSignInButton(
                    text = "Continue with Google",
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val credentialManager = CredentialManager.create(context)
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(webClientId)
                                    .setAutoSelectEnabled(false)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(context, request)
                                val credential = result.credential
                                
                                if (credential is androidx.credentials.CustomCredential &&
                                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    onAction(LoginAction.GoogleLoginSubmitted(googleIdTokenCredential.idToken))
                                }
                            } catch (e: Exception) {
                                Log.e("GoogleSignIn", "Sign-in failed", e)
                                // In a real app, you might want to show a toast or pass the error to the viewmodel
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Icon",
                            tint = Color.Unspecified
                        )
                    }
                )

                LoginFooter(
                    onCreateAccountClick = openRegister,
                    modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
                )
            }
        }
    }
}

