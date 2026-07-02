package com.iti.pocketshop.features.auth.forgetpassword.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.toUserMessage
import com.iti.pocketshop.features.auth.login.presentation.component.AuthTextField
import com.iti.pocketshop.features.auth.login.presentation.component.LoginActionButton

@Composable
fun ForgotPasswordRoot(
    navigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ForgotPasswordScreen(
        state = state,
        onAction = viewModel::onAction,
        navigateBack = navigateBack,
    )
}

@Composable
private fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.forgot_password_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.forgot_password_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            )
            AuthTextField(
                label = stringResource(R.string.login_label_email),
                value = state.email,
                onValueChange = { onAction(ForgotPasswordAction.EmailChanged(it)) },
                placeholder = stringResource(R.string.login_placeholder_email),
                isError = state.emailError || state.error != null,
                errorMessage = when {
                    state.emailError -> stringResource(R.string.error_invalid_email)
                    state.error != null -> state.error.toUserMessage(context)
                    else -> null
                },
            )
            if (state.isSent) {
                Text(
                    text = stringResource(R.string.reset_link_sent),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp),
                )
            } else {
                LoginActionButton(
                    text = stringResource(R.string.forgot_password_submit),
                    onClick = { onAction(ForgotPasswordAction.Submit) },
                    enabled = !state.isLoading,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
            TextButton(onClick = navigateBack, modifier = Modifier.padding(top = 8.dp)) {
                Text(stringResource(R.string.back_to_login))
            }
        }
    }
}
