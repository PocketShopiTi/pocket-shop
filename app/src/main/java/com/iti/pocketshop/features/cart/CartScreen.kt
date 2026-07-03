package com.iti.pocketshop.features.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.presentation.PaymentButton

@Composable
fun CartRoot(
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CartScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun CartScreen(
    state: CartState,
    onAction: (CartAction) -> Unit,
) {
    // Temporary content: the payment test button lives here until the real cart lands.
    var selectedCurrency by remember { mutableStateOf(PaymentCurrency.EGP) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedCurrency == PaymentCurrency.EGP,
                onClick = { selectedCurrency = PaymentCurrency.EGP },
                label = { Text(text = stringResource(R.string.cart_currency_egp)) },
            )
            FilterChip(
                selected = selectedCurrency == PaymentCurrency.USD,
                onClick = { selectedCurrency = PaymentCurrency.USD },
                label = { Text(text = stringResource(R.string.cart_currency_usd)) },
            )
        }
        PaymentButton(
            amountMinor = when (selectedCurrency) {
                PaymentCurrency.EGP -> TEST_AMOUNT_MINOR_EGP
                PaymentCurrency.USD -> TEST_AMOUNT_MINOR_USD
            },
            currency = selectedCurrency,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

// Test amounts in minor units (EGP 150.00 / USD 10.00) until the real cart total exists.
private const val TEST_AMOUNT_MINOR_EGP = 15_000L
private const val TEST_AMOUNT_MINOR_USD = 1_000L
