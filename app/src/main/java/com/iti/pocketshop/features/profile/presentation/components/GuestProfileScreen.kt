package com.iti.pocketshop.features.profile.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.profile.presentation.components.guest.GuestActions
import com.iti.pocketshop.features.profile.presentation.components.guest.GuestAuthActions
import com.iti.pocketshop.features.profile.presentation.components.guest.GuestBenefitCards
import com.iti.pocketshop.features.profile.presentation.components.guest.GuestInfo
import com.iti.pocketshop.features.profile.presentation.components.guest.GuestProfileHeader


@Composable
fun GuestProfileScreen(
    openLogin: () -> Unit,
    openRegister: () -> Unit,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = 24.dp,
        ),
    ) {
        item { GuestProfileHeader(openSettings) }
        item { GuestInfo() }
        item { GuestAuthActions(openRegister, openLogin) }
        item { GuestBenefitCards() }
        item { GuestActions(openSettings) }
    }
}
