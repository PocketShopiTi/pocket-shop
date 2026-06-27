package com.iti.pocketshop.features.login.presentation.view
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    openOTP: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LoginScreen(
        openOTP = openOTP,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun LoginScreen(
    openOTP: () -> Unit,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Continue as guest",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable { onAction(LoginAction.ContinueAsGuestClicked) },
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )

            LoginHeader()

            AuthTextField(
                label = "Email",
                value = state.email,
                onValueChange = { onAction(LoginAction.EmailChanged(it)) },
                placeholder = "sofia@example.com",
                modifier = Modifier.padding(top = 32.dp),
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) }
            )

            PasswordField(
                value = state.password,
                onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
                modifier = Modifier.padding(top = 16.dp)
            )

            ForgotPasswordLink(
                onClick = { onAction(LoginAction.ForgotPasswordClicked) },
                modifier = Modifier.padding(top = 8.dp)
            )

            LoginActionButton(
                text = "Log in",
                onClick = { onAction(LoginAction.LoginClicked) },
                enabled = !state.isLoading,
                modifier = Modifier.padding(top = 16.dp)
            )

            DividerWithText(
                text = "or continue with",
                modifier = Modifier.padding(vertical = 24.dp)
            )

            SocialSignInButton(
                text = "Continue with Google",
                onClick = { onAction(LoginAction.GoogleLoginClicked) },
            )

            LoginFooter(
                onCreateAccountClick = { onAction(LoginAction.CreateAccountClicked) },
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
            )
        }
    }
}
