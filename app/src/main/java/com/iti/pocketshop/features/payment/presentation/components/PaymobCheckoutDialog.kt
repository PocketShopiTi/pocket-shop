package com.iti.pocketshop.features.payment.presentation.components

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.iti.pocketshop.R
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession
import com.iti.pocketshop.features.payment.presentation.PaymentAction


@Composable
internal fun PaymobCheckoutDialog(
    session: PaymobPaymentSession,
    onAction: (PaymentAction) -> Unit,
) {
    Dialog(
        onDismissRequest = { onAction(PaymentAction.PaymobCheckoutDismissed) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.payment_checkout_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    IconButton(
                        onClick = { onAction(PaymentAction.PaymobCheckoutDismissed) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.payment_checkout_close),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                PaymobCheckoutWebView(
                    session = session,
                    onAction = onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PaymobCheckoutWebView(
    session: PaymobPaymentSession,
    onAction: (PaymentAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean {
                        val url = request?.url ?: return false
                        if (!url.toString().startsWith(session.redirectUrl)) return false
                        onAction(url.toCheckoutFinished())
                        return true
                    }
                }
                loadUrl(session.checkoutUrl)
            }
        },
    )
}

private fun Uri.toCheckoutFinished(): PaymentAction.PaymobCheckoutFinished =
    PaymentAction.PaymobCheckoutFinished(
        success = getQueryParameter("success")?.equals("true", ignoreCase = true) == true,
        transactionId = getQueryParameter("id"),
    )
