package com.iti.pocketshop.features.login.domain.model

import androidx.annotation.StringRes
import com.iti.pocketshop.R

enum class LoginError(@StringRes val resId: Int) {

    INVALID_EMAIL(R.string.error_invalid_email),
    PASSWORD_TOO_SHORT(R.string.error_password_too_short),

    GOOGLE_SIGN_IN_FAILED(R.string.error_google_sign_in_failed),
    GOOGLE_SIGN_IN_CANCELLED(R.string.error_google_sign_in_cancelled),

    WRONG_PASSWORD(R.string.error_wrong_password),
    USER_NOT_FOUND(R.string.error_user_not_found),
    NETWORK_ERROR(R.string.error_network),
    UNKNOWN(R.string.error_unknown)
}