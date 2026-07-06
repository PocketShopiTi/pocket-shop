package com.iti.pocketshop.features.checkout.domain.model

import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.payment.domain.models.UserData


fun Address.toUserData(email: String?) : UserData {
    return UserData(
        firstName = firstName,
        lastName = lastName,
        email = email ?: phone,
        phoneNumber = phone,
    )
}