package com.iti.pocketshop.features.checkout.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.iti.pocketshop.LocalSettingsUser
import com.iti.pocketshop.LocalUser
import com.iti.pocketshop.R
import com.iti.pocketshop.core.pricing.PriceFormatter
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.checkout.data.mappers.Order
import com.iti.pocketshop.features.checkout.data.mappers.PaymentConfirmation
import com.iti.pocketshop.features.checkout.domain.model.PaymentMethod
import com.iti.pocketshop.features.checkout.domain.model.toUserData
import com.iti.pocketshop.features.checkout.presentation.components.PaymentMethodCard
import com.iti.pocketshop.features.home.domain.models.Money
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.PaymentGateway
import com.iti.pocketshop.features.payment.presentation.PaymentButton
import kotlin.math.roundToLong


@Composable
fun OrderCheckoutRoot(
    onOrderPlaced: (Order) -> Unit,
    onBack: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.placedOrder) {
        state.placedOrder?.let {
            onOrderPlaced(it)
        }
    }

    OrderCheckoutScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction
    )
}

@Composable
fun OrderCheckoutScreen(
    state: CheckoutState,
    onBack: () -> Unit,
    onAction: (CheckoutAction) -> Unit,
) {
    if (state.isProcessingOrder) {
        OrderProcessingDialog()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.checkout),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                state = state,
                selectedAddress = state.selectedAddress,
                onAction = onAction
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OrderSummaryCard(state = state)
                    }
                    item {
                        CouponCard(
                            state = state,
                            onAction = onAction
                        )
                    }
                    item {
                        PaymentMethodCard(
                            selectedMethod = state.selectedPaymentMethod,
                            onSelect = { onAction(CheckoutAction.SelectPaymentMethod(it)) }
                        )
                    }
                    item {
                        AddressSection(
                            state = state,
                            onAction = onAction
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderProcessingDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
                Text(
                    text = stringResource(R.string.processing_your_order),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.please_wait_a_moment),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    state: CheckoutState,
) {
    val userSettings = LocalSettingsUser.current

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.order_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider()

            val subtotal = state.cart?.subtotalAmount
            val total = state.cart?.totalAmount
            val discountAmount = if (subtotal != null && total != null) {
                ((subtotal.amount - total.amount) * 100).roundToLong() / 100.0
            } else {
                0.0
            }
            val appliedCodes = state.cart?.appliedDiscountCodes.orEmpty()

            SummaryRow(
                label = stringResource(R.string.subtotal),
                value = subtotal?.let {
                    PriceFormatter.format(it.amount, it.currencyCode.rawValue, userSettings)
                }.orEmpty(),
                labelStyle = MaterialTheme.typography.bodyLarge,
                valueStyle = MaterialTheme.typography.titleMedium,
            )

            if (appliedCodes.isNotEmpty() && discountAmount > 0.0) {
                SummaryRow(
                    label = stringResource(
                        R.string.checkout_discount_with_code,
                        appliedCodes.joinToString()
                    ),
                    value = stringResource(
                        R.string.checkout_negative_amount,
                        PriceFormatter.format(
                            amount = discountAmount,
                            sourceCurrencyCode = total?.currencyCode?.rawValue ?: "USD",
                            userSettings = userSettings,
                        )
                    ),
                    labelStyle = MaterialTheme.typography.bodyLarge,
                    valueStyle = MaterialTheme.typography.titleMedium,
                    valueColor = MaterialTheme.colorScheme.primary,
                )
            }

            SummaryRow(
                label = stringResource(R.string.total),
                value = total?.let {
                    PriceFormatter.format(it.amount, it.currencyCode.rawValue, userSettings)
                }.orEmpty(),
                labelStyle = MaterialTheme.typography.titleMedium,
                valueStyle = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    labelStyle: TextStyle,
    valueStyle: TextStyle,
    valueColor: Color = Color.Unspecified,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = labelStyle,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            value,
            style = valueStyle,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun CouponCard(
    state: CheckoutState,
    onAction: (CheckoutAction) -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.coupons),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (state.cart?.appliedDiscountCodes?.isNotEmpty() == true) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    state.cart.appliedDiscountCodes.forEach { code ->
                        AssistChip(
                            onClick = { onAction(CheckoutAction.RemoveCoupon(code)) },
                            label = { Text(code) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(
                                        R.string.remove_coupon_code,
                                        code
                                    ),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.couponCodeInput,
                        onValueChange = { onAction(CheckoutAction.OnCouponInputChanged(it)) },
                        label = {
                            Text(
                                stringResource(R.string.coupon_code)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    FilledTonalButton(
                        onClick = { onAction(CheckoutAction.ApplyCoupon) },
                        enabled = state.couponCodeInput.isNotBlank() && !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.apply))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressSection(
    state: CheckoutState,
    onAction: (CheckoutAction) -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.delivery_address),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (state.addresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_saved_addresses_yet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.addresses.forEach { address ->
                        val selected = state.selectedAddress?.id == address.id
                        Surface(
                            onClick = { onAction(CheckoutAction.SelectAddress(address)) },
                            shape = MaterialTheme.shapes.medium,
                            color = if (selected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { onAction(CheckoutAction.SelectAddress(address)) }
                                )
                                Column(modifier = Modifier.padding(start = 4.dp)) {
                                    Text(
                                        address.address1,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        address.city,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    state: CheckoutState,
    selectedAddress: Address?,
    onAction: (CheckoutAction) -> Unit,
) {
    val total = state.cart?.totalAmount
    val canCheckout = total != null && total.amount > 0.0 && state.selectedAddress != null
    val user = LocalUser.current

    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 16.dp)) {
            if (state.selectedAddress == null) {
                Text(
                    stringResource(R.string.select_a_delivery_address_to_continue),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (total != null && selectedAddress != null) {
                val user = selectedAddress.toUserData(user?.email)
                val paymentAmount = PriceFormatter.toEgpMoney(
                    amount = total.amount,
                    sourceCurrencyCode = total.currencyCode.rawValue,
                )
                when (state.selectedPaymentMethod) {
                    PaymentMethod.CARD -> PaymentButton(
                        amountMinor = PriceFormatter.toEgpPaymentMinorUnits(
                            amount = total.amount,
                            sourceCurrencyCode = total.currencyCode.rawValue,
                        ),
                        currency = PaymentCurrency.EGP,
                        userData = user,
                        onSuccess = { transactionId ->
                            onAction(
                                CheckoutAction.OnPaymentSuccess(
                                    customer = user,
                                    paymentConfirmation = PaymentConfirmation(
                                        transactionId = transactionId,
                                        gateway = PaymentGateway.PayMob.gatewayName,
                                        amount = Money(
                                            paymentAmount.amount,
                                            paymentAmount.currencyCode,
                                        )
                                    )
                                )
                            )
                        },
                        enabled = canCheckout,
                        modifier = Modifier
                            .height(52.dp)
                            .fillMaxWidth()
                    )

                    PaymentMethod.CASH_ON_DELIVERY -> Button(
                        onClick = { onAction(CheckoutAction.PlaceCodOrder(user)) },
                        enabled = canCheckout && !state.isProcessingOrder,
                        modifier = Modifier
                            .height(52.dp)
                            .fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.checkout_place_order))
                    }
                }
            }
        }
    }
}
