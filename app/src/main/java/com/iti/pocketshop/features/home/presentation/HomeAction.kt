package com.iti.pocketshop.features.home.presentation

sealed interface HomeAction {
    data object FetchData: HomeAction
}