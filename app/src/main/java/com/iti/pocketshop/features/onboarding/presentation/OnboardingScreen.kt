package com.iti.pocketshop.features.onboarding.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.features.onboarding.presentation.components.OnboardingActionButtons
import com.iti.pocketshop.features.onboarding.presentation.components.OnboardingDotsIndicator
import com.iti.pocketshop.features.onboarding.presentation.components.OnboardingHeroImage
import com.iti.pocketshop.ui.theme.LocalExtendedColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
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
    val colors = LocalExtendedColors.current
    val pagerState = rememberPagerState(pageCount = { state.pages.size })

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage != state.currentPage) {
            onAction(OnboardingAction.SwipePage(pagerState.settledPage))
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(
                    onClick = { onAction(OnboardingAction.Login) },
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    userScrollEnabled = true,
                ) { page ->
                    val pageOffset = pagerState.offsetForPage(page)
                    val scale = lerp(0.92f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                    val alpha = lerp(0.4f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                    val item = state.pages[page]
                    val isPageVisible = pagerState.settledPage == page

                    var titleVisible by remember(page) { mutableStateOf(false) }
                    var subtitleVisible by remember(page) { mutableStateOf(false) }

                    LaunchedEffect(isPageVisible) {
                        if (isPageVisible) {
                            delay(850.milliseconds)
                            titleVisible = true
                            delay(200.milliseconds)
                            subtitleVisible = true
                        } else {
                            titleVisible = false
                            subtitleVisible = false
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
                    ) {
                        OnboardingHeroImage(
                            pageIndex = page,
                            imageRes = item.imageRes,
                            visible = isPageVisible,
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AnimatedVisibility(
                            visible = titleVisible,
                            enter = slideInVertically(
                                initialOffsetY = { -it / 2 },
                                animationSpec = tween(500, easing = FastOutSlowInEasing),
                            ) + fadeIn(tween(400)),
                        ) {
                            Text(
                                text = stringResource(item.titleRes),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = colors.textPrimary,
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AnimatedVisibility(
                            visible = subtitleVisible,
                            enter = fadeIn(tween(300)) + slideInVertically(
                                initialOffsetY = { it / 4 },
                                animationSpec = tween(300),
                            ),
                        ) {
                            Text(
                                text = stringResource(item.subtitleRes),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = colors.textSecondary,
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OnboardingDotsIndicator(
                    pageCount = state.pages.size,
                    currentPage = state.currentPage,
                )

                OnboardingActionButtons(
                    isLastPage = state.isLastPage,
                    onNext = { onAction(OnboardingAction.Next) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }
    }
}

private fun PagerState.offsetForPage(page: Int): Float =
    abs((currentPage - page) + currentPageOffsetFraction)
