package com.iti.pocketshop.features.profile.presentation.components.guest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.profile.presentation.components.ProfileSectionLabel


@Composable
fun GuestBenefitCards() {
    ProfileSectionLabel(
        text = stringResource(R.string.profile_why_sign_up),
        modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        GuestBenefitCard(
            title = stringResource(R.string.profile_benefit_favourites_title),
            description = stringResource(R.string.profile_benefit_favourites_description),
            icon = Icons.Outlined.FavoriteBorder,
            modifier = Modifier.weight(1f),
        )
        GuestBenefitCard(
            title = stringResource(R.string.profile_benefit_orders_title),
            description = stringResource(R.string.profile_benefit_orders_description),
            icon = Icons.Outlined.Inventory2,
            modifier = Modifier.weight(1f),
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        GuestBenefitCard(
            title = stringResource(R.string.profile_benefit_checkout_title),
            description = stringResource(R.string.profile_benefit_checkout_description),
            icon = Icons.Outlined.CreditCard,
            modifier = Modifier.weight(1f),
        )
        GuestBenefitCard(
            title = stringResource(R.string.profile_benefit_ai_title),
            description = stringResource(R.string.profile_benefit_ai_description),
            icon = Icons.Outlined.AutoAwesome,
            modifier = Modifier.weight(1f),
        )
    }
}