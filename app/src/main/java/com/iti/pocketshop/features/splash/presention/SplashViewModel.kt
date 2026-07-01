package com.iti.pocketshop.features.splash.presention

import androidx.lifecycle.ViewModel
import com.iti.pocketshop.core.userdata.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return userRepo.isSignedIn
    }

}