package com.iti.pocketshop.features.address.presentation.action


sealed interface AddressEffect {
    data object RequestLocationPermission : AddressEffect
    data object RequestContactsPermission : AddressEffect
    data object LaunchContactPicker : AddressEffect
}