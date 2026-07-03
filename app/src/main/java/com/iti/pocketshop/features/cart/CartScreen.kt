package com.iti.pocketshop.features.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.toUserMessage

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
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(R.string.cart_coupon_test_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.cart_mock_applied_coupons),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = state.cartId,
            onValueChange = { value -> onAction(CartAction.CartIdChanged(value)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.cart_id_label)) },
            singleLine = true,
        )

        OutlinedTextField(
            value = state.couponCode,
            onValueChange = { value -> onAction(CartAction.CouponCodeChanged(value)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.cart_coupon_code_label)) },
            singleLine = true,
        )

        Button(
            onClick = { onAction(CartAction.ApplyCouponClicked) },
            enabled = state.canApplyCoupon,
        ) {
            Text(stringResource(R.string.cart_apply_coupon))
        }

        if (state.isApplyingCoupon) {
            Text(
                text = stringResource(R.string.cart_applying_coupon),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        state.error?.let { error ->
            Text(
                text = error.toUserMessage(context),
                color = MaterialTheme.colorScheme.error,
            )
        }

        state.couponResult?.let { result ->
            result.cart?.let { cart ->
                cart.discountCodes.forEach { discountCode ->
                    val applicability = stringResource(
                        if (discountCode.applicable) {
                            R.string.cart_coupon_applicable
                        } else {
                            R.string.cart_coupon_not_applicable
                        },
                    )
                    Text(
                        text = stringResource(
                            R.string.cart_discount_status,
                            discountCode.code.ifBlank {
                                stringResource(R.string.cart_empty_discount_code)
                            },
                            applicability,
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                Text(
                    text = stringResource(
                        R.string.cart_subtotal,
                        cart.cost.subtotalAmount.amount,
                        cart.cost.subtotalAmount.currencyCode,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(
                        R.string.cart_total,
                        cart.cost.totalAmount.amount,
                        cart.cost.totalAmount.currencyCode,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            result.warnings.forEach { warning ->
                Text(
                    text = stringResource(
                        R.string.cart_coupon_warning,
                        warning.code,
                        warning.message,
                    ),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            result.userErrors.forEach { userError ->
                Text(
                    text = stringResource(
                        R.string.cart_coupon_user_error,
                        userError.code ?: stringResource(R.string.cart_unknown_error_code),
                        userError.message,
                    ),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
