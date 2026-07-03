package com.iti.pocketshop.features.payment.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.toUserMessage
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.presentation.components.PaymobCheckoutDialog


@Composable
fun PaymentButton(
    amountMinor: Long,
    currency: PaymentCurrency,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    state.paymobCheckout?.let { session ->
        PaymobCheckoutDialog(
            session = session,
            onAction = viewModel::onAction,
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { viewModel.onAction(PaymentAction.Pay(amountMinor, currency)) },
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(text = stringResource(R.string.payment_pay_now))
            }
        }
        state.completedPaymentId?.let {
            Text(
                text = stringResource(R.string.payment_completed),
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        state.error?.let { error ->
            Text(
                text = error.toUserMessage(context),
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
