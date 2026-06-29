package com.iti.pocketshop.features.onboarding.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import com.iti.pocketshop.features.onboarding.presentation.components.OnboardingActionButtons
import com.iti.pocketshop.features.onboarding.presentation.components.OnboardingDotsIndicator
import com.iti.pocketshop.features.onboarding.presentation.components.OnboardingHeroImage
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun OnboardingRoot(
    openLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is OnboardingEvent.NavigateToLogin) openLogin()
        }
    }

    OnboardingScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { state.pages.size })


     LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage)
            pagerState.animateScrollToPage(state.currentPage)
    }

     LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage != state.currentPage)
            onAction(OnboardingAction.SwipePage(pagerState.settledPage))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
         if (!state.isLastPage) {
            TextButton(
                onClick = { onAction(OnboardingAction.Skip) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                userScrollEnabled = true,
            ) { page ->
                val scale by animateFloatAsState(
                    targetValue = if (pagerState.currentPage == page) 1f else 0.92f,
                    animationSpec = tween(300),
                    label = "pageScale",
                )
                val alpha by animateFloatAsState(
                    targetValue = if (pagerState.currentPage == page) 1f else 0.5f,
                    animationSpec = tween(300),
                    label = "pageAlpha",
                )

                val item = state.pages[page]

                var titleVisible by remember(page) { mutableStateOf(false) }
                var subtitleVisible by remember(page) { mutableStateOf(false) }

                LaunchedEffect(page) {
                    delay(850.milliseconds)
                    titleVisible = true
                    delay(200.milliseconds)
                    subtitleVisible = true
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .alpha(alpha),
                ) {
                    OnboardingHeroImage(
                        pageIndex = page,
                        imageRes = item.imageRes
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedVisibility(
                        visible = titleVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(500, easing = FastOutSlowInEasing)
                        ) + fadeIn(tween(400))
                    ) {
                        Text(
                            text = stringResource(item.titleRes),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground,
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = subtitleVisible,
                        enter = fadeIn(tween(200))
                    ) {
                        Text(
                            text = stringResource(item.subtitleRes),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OnboardingDotsIndicator(
                pageCount = state.pages.size,
                currentPage = state.currentPage,
            )

            Spacer(modifier = Modifier.height(24.dp))

            OnboardingActionButtons(
                isLastPage = state.isLastPage,
                onNext = { onAction(OnboardingAction.Next) },
                onLogin = { onAction(OnboardingAction.Login) },
                onGuest = { onAction(OnboardingAction.Guest) },
            )
        }
    }
}

