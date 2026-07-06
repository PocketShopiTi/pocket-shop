package com.iti.pocketshop.features.auth.forgetpassword.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.auth.forgetpassword.domain.SendPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordReset: SendPasswordResetUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.EmailChanged -> _state.update {
                it.copy(email = action.value, emailError = false, error = null, isSent = false)
            }

            ForgotPasswordAction.Submit -> submit()
        }
    }

    private fun submit() {
        val email = _state.value.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailError = true) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = sendPasswordReset(email)) {
                is PocketResult.Success -> _state.update {
                    it.copy(isLoading = false, isSent = true)
                }

                is PocketResult.Error -> _state.update {
                    it.copy(isLoading = false, error = result.error)
                }
            }
        }
    }
}
