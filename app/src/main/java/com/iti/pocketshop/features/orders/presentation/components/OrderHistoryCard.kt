package com.iti.pocketshop.features.orders.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.core.utils.toFormattedDate
import com.iti.pocketshop.features.orders.domain.model.OrderItem
import com.iti.pocketshop.features.orders.domain.model.OrderStatus
import com.iti.pocketshop.ui.theme.LocalExtendedColors
import java.text.NumberFormat
import java.util.Currency

@Composable
fun OrderHistoryCard(
    order: OrderItem,
    onView: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onView(order.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                OrderImage(order = order)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.processedAtEpochMillis.toFormattedDate("MMM d, yyyy"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = order.name,
                        modifier = Modifier.padding(top = 2.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    OrderStatusChip(
                        status = order.status,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }

                Text(
                    text = remember(order.total, order.currencyCode) {
                        NumberFormat.getCurrencyInstance().apply {
                            currency = Currency.getInstance(order.currencyCode)
                        }.format(order.total)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.outline,
            )

            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onView(order.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = CircleShape,
                ) {
                    Text(stringResource(R.string.orders_view))
                }
            }
        }
    }
}

@Composable
private fun OrderImage(order: OrderItem) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 72.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (order.imageUrl.isNullOrBlank()) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    painter = painterResource(R.drawable.box),
                    contentDescription = null,
                    modifier = Modifier.padding(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            AsyncImage(
                model = order.imageUrl,
                contentDescription = stringResource(R.string.orders_image_description, order.name),
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        }
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
