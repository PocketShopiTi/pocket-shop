package com.iti.pocketshop.features.auth.login.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.MainActivity
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.toUserMessage
import com.iti.pocketshop.features.auth.login.presentation.component.AuthTextField
import com.iti.pocketshop.features.auth.login.presentation.component.DividerWithText
import com.iti.pocketshop.features.auth.login.presentation.component.ForgotPasswordLink
import com.iti.pocketshop.features.auth.login.presentation.component.LoginActionButton
import com.iti.pocketshop.features.auth.login.presentation.component.LoginFooter
import com.iti.pocketshop.features.auth.login.presentation.component.LoginHeader
import com.iti.pocketshop.features.auth.login.presentation.component.PasswordField
import com.iti.pocketshop.features.auth.login.presentation.component.SocialSignInButton

@Composable
fun LoginRoot(
    openHome: () -> Unit,
    openVerification: () -> Unit,
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                LoginEvent.NavigateHome -> openHome()
                LoginEvent.NavigateVerification -> openVerification()
            }
        }
    }

    val activity = LocalActivity.current as MainActivity

    LoginScreen(
        openRegister = openRegister,
        openForgotPassword = openForgotPassword,
        state = state,
        onAction = viewModel::onAction,
        onGoogleSignInClick = {
            activity.launchGoogleSignIn(
                onTokenReceived = { idToken ->
                    viewModel.onAction(LoginAction.GoogleLoginSubmitted(idToken))
                },
                onError = {
                    viewModel.onAction(LoginAction.GoogleSignInFailed)
                }
            )
        }
    )
}

@Composable
fun LoginScreen(
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onGoogleSignInClick: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                TextButton(
                    onClick = {
                        onAction(LoginAction.ContinueAsGuestClicked)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = stringResource(R.string.login_continue_as_guest),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                LoginHeader()
            }

            item {
                AuthTextField(
                    label = stringResource(R.string.login_label_email),
                    value = state.email,
                    onValueChange = { onAction(LoginAction.EmailChanged(it)) },
                    placeholder = stringResource(R.string.login_placeholder_email),
                    modifier = Modifier.padding(top = 20.dp),
                    isError = state.emailError,
                    errorMessage = if (state.emailError) {
                        stringResource(R.string.error_invalid_email)
                    } else null,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = null
                        )
                    },
                    keyboardType = KeyboardType.Email,
                )
            }

            item {
                PasswordField(
                    value = state.password,
                    onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
                    modifier = Modifier.padding(top = 16.dp),
                    isError = state.passwordError,
                    errorMessage = when {
                        state.passwordError -> stringResource(R.string.error_wrong_password)
                        state.generalError != null -> state.generalError.toUserMessage(context)
                        else -> null
                    },
                )
            }

            item {
                ForgotPasswordLink(
                    onClick = openForgotPassword,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                LoginActionButton(
                    text = stringResource(R.string.login_button_login),
                    onClick = { onAction(LoginAction.LoginClicked) },
                    enabled = !state.isLoading,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            item {
                DividerWithText(
                    text = stringResource(R.string.login_divider_or_continue),
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
            item {
                SocialSignInButton(
                    text = stringResource(R.string.login_button_google),
                    onClick = onGoogleSignInClick,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = stringResource(R.string.login_google_icon_description),
                            tint = Color.Unspecified
                        )
                    }
                )
            }

            item {
                LoginFooter(
                    onCreateAccountClick = openRegister,
                    modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
                )
            }
        }
    }
}
