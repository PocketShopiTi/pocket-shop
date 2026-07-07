package com.iti.pocketshop.features.orders.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.toUserMessage
import com.iti.pocketshop.features.orders.domain.OrderTimeline
import com.iti.pocketshop.core.utils.toFormattedDate
import com.iti.pocketshop.features.orders.domain.model.OrderDetails
import com.iti.pocketshop.features.orders.domain.model.OrderDetailsAddress
import com.iti.pocketshop.features.orders.domain.model.OrderDetailsLineItem
import com.iti.pocketshop.features.orders.domain.model.OrderPaymentStatus
import com.iti.pocketshop.features.orders.domain.model.OrderPriceBreakdown
import com.iti.pocketshop.features.orders.domain.model.OrderStatus
import com.iti.pocketshop.features.orders.presentation.components.OrdersLoadingContent
import com.iti.pocketshop.features.orders.presentation.components.OrdersTopBar
import com.iti.pocketshop.features.profile.presentation.components.ProfileErrorCard
import com.iti.pocketshop.ui.theme.LocalExtendedColors
import java.text.NumberFormat
import java.util.Currency

@Composable
fun OrderDetailsRoot(
    orderId: String,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit = {},
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.onAction(OrderDetailsAction.OrderChanged(orderId))
    }

    OrderDetailsScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onProductClick = onProductClick,
    )
}

@Composable
fun OrderDetailsScreen(
    state: OrderDetailsState,
    onAction: (OrderDetailsAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val order = state.order

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        OrdersTopBar(
            title = order?.let { stringResource(R.string.orders_detail_title, it.name) }
                ?: stringResource(R.string.orders_detail_fallback_title),
            onBack = onBack,
        )

        when {
            state.isLoading && order == null -> OrdersLoadingContent(
                modifier = Modifier.weight(1f),
            )

            state.error != null && order == null -> Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                ProfileErrorCard(
                    message = state.error.toUserMessage(context),
                    onReload = { onAction(OrderDetailsAction.Retry) },
                )
            }

            order == null -> Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.orders_detail_not_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> OrderDetailsContent(
                order = order,
                onProductClick = onProductClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OrderDetailsContent(
    order: OrderDetails,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "summary") {
            OrderSummaryCard(order = order)
        }

        item(key = "items_title") {
            SectionTitle(text = stringResource(R.string.orders_detail_items))
        }

        item(key = "items") {
            OrderItemsCard(
                items = order.lineItems,
                onProductClick = onProductClick,
            )
        }

        order.shippingAddress?.let { address ->
            item(key = "address_title") {
                SectionTitle(text = stringResource(R.string.orders_detail_delivery_address))
            }
            item(key = "address") {
                DeliveryAddressCard(address = address)
            }
        }

        item(key = "breakdown") {
            PriceBreakdownCard(priceBreakdown = order.priceBreakdown)
        }
    }
}

@Composable
private fun OrderSummaryCard(order: OrderDetails) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = order.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                OrderStatusChip(status = order.status)
            }

            Text(
                text = stringResource(
                    R.string.orders_detail_placed_date,
                    order.processedAtEpochMillis.toFormattedDate("MMM d, yyyy"),
                ),
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LinearProgressIndicator(
                progress = { order.status.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .height(6.dp)
                    .clip(CircleShape),
                color = order.status.progressColor(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.orders_detail_progress_ordered),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.orders_detail_progress_processing),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.orders_detail_progress_delivered),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (order.status != OrderStatus.CANCELLED) {
                        Text(
                            text = OrderTimeline
                                .deliveryDateEpochMillis(order.processedAtEpochMillis)
                                .toFormattedDate("MMM d"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun OrderItemsCard(
    items: List<OrderDetailsLineItem>,
    onProductClick: (String) -> Unit,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                OrderLineItemRow(
                    item = item,
                    onProductClick = onProductClick,
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun OrderLineItemRow(
    item: OrderDetailsLineItem,
    onProductClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                item.productId?.let { productId ->
                    Modifier.clickable { onProductClick(productId) }
                } ?: Modifier
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (item.imageUrl.isNullOrBlank()) {
                Icon(
                    painter = painterResource(R.drawable.box),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.imageAltText
                        ?: stringResource(R.string.orders_detail_item_image, item.title),
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!item.vendor.isNullOrBlank()) {
                Text(
                    text = item.vendor.uppercase(),
                    modifier = Modifier.padding(top = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalExtendedColors.current.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = item.detailText(),
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = rememberMoney(item.total, item.currencyCode),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
    }
}

@Composable
private fun DeliveryAddressCard(address: OrderDetailsAddress) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (address.recipientName.isNotBlank()) {
                Text(
                    text = address.recipientName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = address.addressLines.joinToString(separator = "\n"),
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!address.phone.isNullOrBlank()) {
                Text(
                    text = address.phone,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PriceBreakdownCard(priceBreakdown: OrderPriceBreakdown) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.orders_detail_price_breakdown),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))
            PriceRow(
                label = stringResource(R.string.orders_detail_subtotal),
                value = rememberMoney(
                    priceBreakdown.subtotalBeforeDiscount,
                    priceBreakdown.currencyCode,
                ),
            )
            if (priceBreakdown.discount > 0.0) {
                PriceRow(
                    label = priceBreakdown.discountLabel?.let {
                        stringResource(R.string.orders_detail_discount_with_label, it)
                    } ?: stringResource(R.string.orders_detail_discount),
                    value = stringResource(
                        R.string.orders_detail_negative_amount,
                        rememberMoney(priceBreakdown.discount, priceBreakdown.currencyCode),
                    ),
                    valueColor = MaterialTheme.colorScheme.primary,
                )
            }
            PriceRow(
                label = stringResource(R.string.orders_detail_shipping),
                value = if (priceBreakdown.shipping == 0.0) {
                    stringResource(R.string.orders_detail_free)
                } else {
                    rememberMoney(priceBreakdown.shipping, priceBreakdown.currencyCode)
                },
                valueColor = if (priceBreakdown.shipping == 0.0) {
                    LocalExtendedColors.current.success
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            if (priceBreakdown.tax > 0.0) {
                PriceRow(
                    label = stringResource(R.string.orders_detail_tax),
                    value = rememberMoney(priceBreakdown.tax, priceBreakdown.currencyCode),
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline,
            )
            PriceRow(
                label = stringResource(R.string.orders_detail_total),
                value = rememberMoney(priceBreakdown.total, priceBreakdown.currencyCode),
                labelStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                valueStyle = MaterialTheme.typography.titleMedium,
            )
            if (priceBreakdown.paymentStatus != OrderPaymentStatus.UNKNOWN) {
                PriceRow(
                    label = stringResource(R.string.orders_detail_payment),
                    value = when (priceBreakdown.paymentStatus) {
                        OrderPaymentStatus.PAID -> stringResource(R.string.orders_detail_payment_paid)
                        OrderPaymentStatus.CASH_ON_DELIVERY -> stringResource(R.string.orders_detail_payment_cod)
                        OrderPaymentStatus.REFUNDED -> stringResource(R.string.orders_detail_payment_refunded)
                        OrderPaymentStatus.UNKNOWN -> "" // unreachable: guarded by the surrounding if
                    },
                    valueColor = when (priceBreakdown.paymentStatus) {
                        OrderPaymentStatus.PAID -> LocalExtendedColors.current.success
                        OrderPaymentStatus.CASH_ON_DELIVERY -> LocalExtendedColors.current.info
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    labelStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelLarge,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = labelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            modifier = Modifier.padding(start = 16.dp),
            style = valueStyle,
            color = valueColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun OrderStatusChip(
    status: OrderStatus,
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current
    val (label, color) = when (status) {
        OrderStatus.ORDERED -> stringResource(R.string.orders_status_ordered) to colors.info
        OrderStatus.PROCESSING -> stringResource(R.string.orders_status_processing) to colors.warning
        OrderStatus.FULFILLED -> stringResource(R.string.orders_status_delivered) to colors.success
        OrderStatus.CANCELLED -> stringResource(R.string.orders_status_cancelled) to colors.error
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color.copy(alpha = 0.1f),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

@Composable
private fun OrderDetailsLineItem.detailText(): String {
    val optionSeparator = stringResource(R.string.orders_detail_options_separator)
    val optionTexts = mutableListOf<String>()
    for (option in selectedOptions) {
        optionTexts += stringResource(
            R.string.orders_detail_option_value,
            option.name,
            option.value
        )
    }
    val options = optionTexts.joinToString(separator = optionSeparator)

    return if (options.isBlank()) {
        stringResource(R.string.orders_detail_quantity, quantity)
    } else {
        stringResource(R.string.orders_detail_item_meta, options, quantity)
    }
}

@Composable
private fun rememberMoney(amount: Double, currencyCode: String): String =
    remember(amount, currencyCode) {
        runCatching {
            NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(currencyCode)
            }.format(amount)
        }.getOrNull()
    } ?: stringResource(R.string.orders_detail_amount_fallback, amount, currencyCode)

private val OrderStatus.progress: Float
    get() = when (this) {
        OrderStatus.ORDERED -> 0.33f
        OrderStatus.PROCESSING -> 0.66f
        OrderStatus.FULFILLED -> 1f
        OrderStatus.CANCELLED -> 0f
    }

@Composable
private fun OrderStatus.progressColor(): androidx.compose.ui.graphics.Color {
    val colors = LocalExtendedColors.current
    return when (this) {
        OrderStatus.ORDERED -> colors.info
        OrderStatus.PROCESSING -> colors.warning
        OrderStatus.FULFILLED -> colors.success
        OrderStatus.CANCELLED -> colors.error
    }
}
