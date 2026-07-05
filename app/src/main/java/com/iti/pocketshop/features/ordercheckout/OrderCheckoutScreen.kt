package com.iti.pocketshop.features.ordercheckout

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.features.checkout.domain.model.PaymentMethod

@Composable
fun OrderCheckoutRoot(
    viewModel: OrderCheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OrderCheckoutScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCheckoutScreen(
    state: OrderCheckoutState,
    onAction: (OrderCheckoutAction) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(state.checkoutUrl) {
        if (state.checkoutUrl != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.checkoutUrl))
            context.startActivity(intent)
            onAction(OrderCheckoutAction.CheckoutHandled)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cart Summary
                Text("Subtotal: ${state.cart?.subtotalAmount} ${state.cart?.subtotalCurrencyCode}", style = MaterialTheme.typography.titleMedium)
                Text("Total: ${state.cart?.totalAmount} ${state.cart?.totalCurrencyCode}", style = MaterialTheme.typography.titleLarge)
                
                // Applied Coupons
                if (state.cart?.appliedDiscountCodes?.isNotEmpty() == true) {
                    Text("Applied Coupons:", style = MaterialTheme.typography.bodyMedium)
                    state.cart.appliedDiscountCodes.forEach { code ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(code, modifier = Modifier.weight(1f))
                            TextButton(onClick = { onAction(OrderCheckoutAction.RemoveCoupon(code)) }) {
                                Text("Remove")
                            }
                        }
                    }
                }

                // Apply Coupon
                OutlinedTextField(
                    value = state.couponCodeInput,
                    onValueChange = { onAction(OrderCheckoutAction.OnCouponInputChanged(it)) },
                    label = { Text("Coupon Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { onAction(OrderCheckoutAction.ApplyCoupon) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Apply Coupon")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Addresses
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Delivery Address", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { onAction(OrderCheckoutAction.AddTestAddress) }) {
                        Text("Add Test Address")
                    }
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.addresses) { address ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.selectedAddressId == address.id,
                                onClick = { onAction(OrderCheckoutAction.SelectAddress(address.id)) }
                            )
                            Text("${address.address1}, ${address.city}", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                // Payment Methods
                Text("Payment Method", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                        onClick = { onAction(OrderCheckoutAction.SelectPaymentMethod(PaymentMethod.CASH_ON_DELIVERY)) }
                    )
                    Text("Cash on Delivery")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = state.selectedPaymentMethod == PaymentMethod.ONLINE_PAYMENT,
                        onClick = { onAction(OrderCheckoutAction.SelectPaymentMethod(PaymentMethod.ONLINE_PAYMENT)) }
                    )
                    Text("Online Payment")
                }

                // Place Order Button
                Button(
                    onClick = { onAction(OrderCheckoutAction.PlaceOrder) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.selectedAddressId != null
                ) {
                    Text("Place Order")
                }
            }
        }
    }
}
