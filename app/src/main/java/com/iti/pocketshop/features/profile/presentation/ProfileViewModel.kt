package com.iti.pocketshop.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.features.profile.domain.usecase.GetCurrentUserDataUseCase
import com.iti.pocketshop.features.profile.domain.usecase.GetProfileStatsUseCase
import com.iti.pocketshop.features.profile.domain.usecase.GetRecentOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
    private val getRecentOrdersUseCase: GetRecentOrdersUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadAllData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    private fun loadAllData() {
        viewModelScope.launch {
            fetchUserData()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            errorMessage = null
                        )
                    }

                    fetchProfileStats(user.id)
                    fetchRecentOrders(user.id)
                }.onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.name
                        )
                    }
                }
        }
    }

    private suspend fun fetchUserData(): PocketResult<UserEntity, PocketDataError.Remote> {
        return getCurrentUserDataUseCase()
    }

    private fun fetchProfileStats(userId: String) {
        viewModelScope.launch {
            getProfileStatsUseCase(userId).onSuccess { stats ->
                _state.update {
                    it.copy(
                        userStats = stats
                    )
                }
            }
        }
    }

    private fun fetchRecentOrders(userId: String) {
        viewModelScope.launch {
            getRecentOrdersUseCase(userId).onSuccess { orders ->
                _state.update {
                    it.copy(
                        orders = orders
                    )
                }
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Retry -> loadAllData()
        }
    }

}