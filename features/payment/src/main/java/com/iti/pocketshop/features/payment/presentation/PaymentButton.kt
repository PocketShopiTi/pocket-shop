package com.iti.pocketshop.features.payment.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.features.payment.R
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.ui.PaymobSdkListener

@Composable
fun PaymentButton(
    amountMinor: Long,
    currency: PaymentCurrency,
    userData: UserData?,
    enabled: Boolean,
    onSuccess: (transactionId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var startedIntentionId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(state.completedPaymentId) {
        state.completedPaymentId?.let { id ->
            onSuccess(id)
        }
    }

    LaunchedEffect(state.paymobCheckout) {
        val session = state.paymobCheckout
        if (session != null && session.intentionId != startedIntentionId) {
            startedIntentionId = session.intentionId
            PaymobSdk.Builder(
                context = context,
                clientSecret = session.clientSecret,
                publicKey = session.publicKey,
                paymobSdkListener = object : PaymobSdkListener {
                    override fun onSuccess(payResponse: HashMap<String, String?>) {
                        viewModel.onAction(PaymentAction.OnPaymobSuccess(payResponse))
                    }

                    override fun onFailure(msg: String?) {
                        viewModel.onAction(PaymentAction.OnPaymobFailure(msg))
                    }

                    override fun onPending() {
                        // Kiosk payment
                    }
                }
            ).build().start()
        }
    }

    Button(
        onClick = {
            userData?.let {
                viewModel.onAction(PaymentAction.Pay(amountMinor, currency, userData))
            }
        },
        enabled = enabled && !state.isLoading,
        modifier = modifier,
        shape = CircleShape,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 3.dp,
            )
        } else {
            Text(
                text = stringResource(R.string.payment_pay_now),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

/* Example of how to use it
PaymentButton(
    amountMinor = 5000_00,
    currency = PaymentCurrency.EGP,
    userData = UserData(
        firstName = "Hossam",
        lastName = "Elgmmal",
        email = "hossam@gmail.com",
        phoneNumber = "+201010101010"
    ),
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 20.dp),
    onResult = {
        // TODO Handle success only
    },
)
* */
