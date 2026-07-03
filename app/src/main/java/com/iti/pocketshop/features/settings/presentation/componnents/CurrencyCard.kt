package com.iti.pocketshop.features.settings.presentation.componnents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.common.settings.domain.models.CurrencySetting


@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun CurrencyCard(
    selectedUnit: CurrencySetting,
    onAction: (CurrencySetting) -> Unit
) {
    val context = LocalContext.current.applicationContext
    OutlinedCard (
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.currency_unit),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
            )
            ButtonGroup(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth(),
                overflowIndicator = {}
            ) {
                CurrencySetting.entries.forEach { currencyUnit ->
                    toggleableItem(
                        weight = 1f,
                        checked = selectedUnit == currencyUnit,
                        onCheckedChange = {
                            if (it) {
                                onAction(currencyUnit)
                            }
                        },
                        label = context.getString(currencyUnit.getTitleId()),
                    )
                }
            }
        }
    }
}