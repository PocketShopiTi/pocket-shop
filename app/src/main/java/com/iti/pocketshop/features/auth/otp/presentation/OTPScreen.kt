package com.iti.pocketshop.features.auth.otp.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.network.toUserMessage

@Composable
fun EmailVerificationRoot(
    openLogin: () -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                EmailVerificationEvent.NavigateLogin -> openLogin()
            }
        }
    }
    EmailVerificationScreen(state = state, onAction = viewModel::onAction)
}

@Composable
private fun EmailVerificationScreen(
    state: EmailVerificationState,
    onAction: (EmailVerificationAction) -> Unit,
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
                text = stringResource(R.string.verify_email_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.verify_email_description, state.email),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            )
            state.error?.let {
                Text(
                    text = it.toUserMessage(context),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            if (state.isNotVerifiedYet) {
                Text(
                    text = stringResource(R.string.not_verified_yet),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            if (state.resendSucceeded) {
                Text(
                    text = stringResource(R.string.verify_email_sent),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            Button(
                onClick = { onAction(EmailVerificationAction.CheckVerification) },
                enabled = !state.isChecking,
            ) {
                Text(stringResource(R.string.check_verification))
            }
            TextButton(
                onClick = { onAction(EmailVerificationAction.ResendEmail) },
                enabled = state.resendCooldown == 0,
            ) {
                Text(
                    if (state.resendCooldown > 0) {
                        stringResource(R.string.resend_in_seconds, state.resendCooldown)
                    } else {
                        stringResource(R.string.resend_verification)
                    }
                )
            }
            TextButton(onClick = { onAction(EmailVerificationAction.BackToLogin) }) {
                Text(stringResource(R.string.back_to_login))
            }
        }
    }
}
