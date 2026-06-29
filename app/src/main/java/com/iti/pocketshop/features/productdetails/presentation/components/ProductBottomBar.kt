package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsAction

@Composable
internal fun ProductBottomBar(
    quantity: Int,
    totalPrice: Money?,
    isEnabled: Boolean,
    isAddedToCart: Boolean,
    onAction: (ProductDetailsAction) -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            QuantityController(onAction = onAction, quantity = quantity)
            Button(
                onClick = { onAction(ProductDetailsAction.AddToCartClicked) },
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                AnimatedContent(
                    targetState = isAddedToCart,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith
                                (fadeOut() + scaleOut(targetScale = 0.8f))
                    },
                    label = "addToCartFeedback",
                ) { added ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (added) CheckIcon() else BagIcon()
                        Text(
                            text = stringResource(
                                if (added) R.string.product_details_added_to_cart
                                else R.string.product_details_add_to_cart,
                                formatMoney(totalPrice),
                            ),
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuantityController(
    quantity: Int,
    onAction: (ProductDetailsAction) -> Unit,
) {
    val quantityDescription = stringResource(R.string.product_details_quantity, quantity)

    Surface(
        modifier = Modifier.height(44.dp),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            QuantityButton(
                description = stringResource(R.string.product_details_decrease_quantity),
                onClick = { onAction(ProductDetailsAction.DecreaseQuantity) },
            ) { MinusIcon() }
            Text(
                text = quantity.toString(),
                modifier = Modifier.semantics {
                    contentDescription = quantityDescription
                },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            QuantityButton(
                description = stringResource(R.string.product_details_increase_quantity),
                onClick = { onAction(ProductDetailsAction.IncreaseQuantity) },
            ) { PlusIcon() }
        }
    }
}

@Composable
private fun QuantityButton(
    description: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .semantics { contentDescription = description }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { icon() }
}

