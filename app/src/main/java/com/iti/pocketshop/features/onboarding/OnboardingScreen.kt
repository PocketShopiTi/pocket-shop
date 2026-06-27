package com.iti.pocketshop.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun OnboardingRoot(
    openLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingScreen(
        openLogin = openLogin,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun OnboardingScreen(
    openLogin: () -> Unit,
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
) {
    Column (
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "onboarding Screen")
        Button(
            onClick = openLogin
        ) {
            Text(text = "Continue")
        }
    }
}
