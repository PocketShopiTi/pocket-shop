package com.iti.pocketshop.features.onboardingnotification.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.features.onboardingnotification.presentation.componet.OnboardingNotificationContent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingNotificationRoot(
    adId: String,
    openHome: () -> Unit,
    viewModel: OnboardingNotificationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(adId) {
        viewModel.onAction(OnboardingNotificationAction.Load(adId))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                OnboardingNotificationEvent.NavigateHome -> openHome()
            }
        }
    }

    OnboardingNotificationScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun OnboardingNotificationScreen(
    state: OnboardingNotificationState,
    onAction: (OnboardingNotificationAction) -> Unit,
) {
    OnboardingNotificationContent(
        state = state,
        onAction = onAction,
    )
}
