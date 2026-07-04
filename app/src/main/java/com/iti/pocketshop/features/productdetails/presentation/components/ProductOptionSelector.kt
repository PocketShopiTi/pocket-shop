package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOption
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionType
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsAction
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProductOptionSelector(
    option: ProductOption,
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
) {
    val selectedId = state.selectedOptionValueIds[option.id]
    val selectedLabel = option.values.firstOrNull { it.id == selectedId }?.label.orEmpty()
    val sectionTitle = when (option.type) {
        ProductOptionType.COLOR -> stringResource(R.string.product_details_colour, selectedLabel)
        ProductOptionType.SIZE -> stringResource(R.string.product_details_size, selectedLabel)
        ProductOptionType.GENERIC -> stringResource(
            R.string.product_details_generic_option,
            option.name,
            selectedLabel,
        )
    }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        AnimatedContent(
            targetState = sectionTitle,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "optionSectionTitle",
        ) { currentTitle ->
            Text(
                text = currentTitle.uppercase(Locale.getDefault()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = sectionLabelStyle(),
            )
        }
        FlowRow(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(
                if (option.type == ProductOptionType.COLOR) 12.dp else 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            option.values.forEach { value ->
                val isSelected = selectedId == value.id
                val isAvailable = state.isOptionValueAvailable(option.id, value.id)
                val onClick = {
                    onAction(ProductDetailsAction.OptionSelected(option.id, value.id))
                }
                when (option.type) {
                    ProductOptionType.COLOR -> ColourSwatch(
                        colour = value.swatchArgb?.let(::Color),
                        imageUrl = value.swatchImage?.url,
                        label = value.label,
                        selected = isSelected,
                        enabled = isAvailable,
                        onClick = onClick,
                    )
                    ProductOptionType.SIZE -> SizeOption(
                        label = value.label,
                        selected = isSelected,
                        enabled = isAvailable,
                        onClick = onClick,
                    )
                    ProductOptionType.GENERIC -> GenericOption(
                        label = value.label,
                        selected = isSelected,
                        enabled = isAvailable,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}
