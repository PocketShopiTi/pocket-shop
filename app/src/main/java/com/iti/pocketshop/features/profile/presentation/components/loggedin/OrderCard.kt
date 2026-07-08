package com.iti.pocketshop.features.profile.presentation.components.loggedin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.LocalSettingsUser
import com.iti.pocketshop.R
import com.iti.pocketshop.core.pricing.PriceFormatter
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.features.profile.domain.model.OrderStatus.CANCELLED
import com.iti.pocketshop.features.profile.domain.model.OrderStatus.FULFILLED
import com.iti.pocketshop.features.profile.domain.model.OrderStatus.ORDERED
import com.iti.pocketshop.features.profile.domain.model.OrderStatus.PROCESSING
import com.iti.pocketshop.ui.theme.LocalExtendedColors


@Composable
fun RecentOrderCard(
    order: OrderEntity,
    onClick: (String) -> Unit,
) {
    val userSettings = LocalSettingsUser.current

    Surface(
        modifier = Modifier
            .width(148.dp)
            .heightIn(min = 188.dp)
            .clickable { onClick(order.id) },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (order.imageUrl.isNullOrBlank()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    AsyncImage(
                        model = order.imageUrl,
                        contentDescription = stringResource(R.string.profile_order_image, order.id),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = order.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OrderStatusChip(order.status, Modifier.padding(top = 6.dp))
                Text(
                    text = PriceFormatter.format(order.total, order.currencyCode, userSettings),
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun OrderStatusChip(status: OrderStatus, modifier: Modifier = Modifier) {
    val extendedColors = LocalExtendedColors.current
    val (label, color) = when (status) {
        ORDERED -> stringResource(R.string.profile_status_ordered) to extendedColors.info
        PROCESSING -> stringResource(R.string.profile_status_processing) to extendedColors.warning
        CANCELLED -> stringResource(R.string.profile_status_cancelled) to extendedColors.error
        FULFILLED -> stringResource(R.string.profile_status_delivered) to extendedColors.success
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.1f),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = color,
        )
    }
}
