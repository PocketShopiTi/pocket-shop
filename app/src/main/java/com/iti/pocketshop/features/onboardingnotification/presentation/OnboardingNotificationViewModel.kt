package com.iti.pocketshop.features.onboardingnotification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.onboardingnotification.domain.usecase.GetNotificationAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingNotificationViewModel @Inject constructor(
    private val getNotificationAd: GetNotificationAdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingNotificationState())
    val state = _state
        .onStart { }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OnboardingNotificationState(),
        )

    private val _events = Channel<OnboardingNotificationEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: OnboardingNotificationAction) {
        when (action) {
            is OnboardingNotificationAction.Load -> loadAd(action.adId)
            OnboardingNotificationAction.Retry -> loadAd(_state.value.adId, force = true)
            OnboardingNotificationAction.Next -> goNext()
            OnboardingNotificationAction.Home -> navigateHome()
            is OnboardingNotificationAction.SwipePage -> updatePage(action.page)
        }
    }

    private fun loadAd(adId: String, force: Boolean = false) {
        if (adId.isBlank()) return
        if (!force && _state.value.adId == adId && _state.value.hasLoaded) return

        _state.update {
            it.copy(
                adId = adId,
                isLoading = true,
                hasLoaded = false,
                ad = null,
                error = null,
                currentPage = 0,
            )
        }

        viewModelScope.launch {
            getNotificationAd(adId)
                .onSuccess { ad ->
                    if (_state.value.adId != adId) return@onSuccess
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasLoaded = true,
                            ad = ad,
                            error = null,
                        )
                    }
                }
                .onError { error ->
                    if (_state.value.adId != adId) return@onError
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasLoaded = true,
                            error = error,
                        )
                    }
                }
        }
    }

    private fun goNext() {
        if (_state.value.isLastPage) {
            navigateHome()
            return
        }

        _state.update {
            it.copy(
                currentPage = (it.currentPage + 1)
                    .coerceAtMost(OnboardingNotificationState.PAGE_COUNT - 1),
            )
        }
    }

    private fun updatePage(page: Int) {
        _state.update {
            it.copy(
                currentPage = page.coerceIn(0, OnboardingNotificationState.PAGE_COUNT - 1),
            )
        }
    }

    private fun navigateHome() {
        viewModelScope.launch {
            _events.send(OnboardingNotificationEvent.NavigateHome)
        }
    }
}
