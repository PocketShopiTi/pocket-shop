package com.iti.pocketshop.features.settings.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.LocalSettingsUser
import com.iti.pocketshop.R
import com.iti.pocketshop.features.settings.presentation.action.SettingsAction
import com.iti.pocketshop.features.settings.presentation.state.SettingsState
import com.iti.pocketshop.features.settings.presentation.viewmodel.SettingsViewModel
import com.iti.pocketshop.features.settings.presentation.componnents.CurrencyCard
import com.iti.pocketshop.features.settings.presentation.componnents.LanguageDialog
import com.iti.pocketshop.features.settings.presentation.componnents.OpenDialogSettingsCard
import com.iti.pocketshop.features.settings.presentation.componnents.ThemeDialog

@Composable
fun SettingsRoot(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        onBack = onBack,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    val userSettings = LocalSettingsUser.current
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null,
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.Companion
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OpenDialogSettingsCard(
                    onAction = { onAction(SettingsAction.LanguageDialogToggle(true)) },
                    iconId = R.drawable.ic_language,
                    titleId = R.string.language,
                    valueId = userSettings.language.getTitleId(),
                )
            }
            item {
                OpenDialogSettingsCard(
                    onAction = { onAction(SettingsAction.ThemeDialogToggle(true)) },
                    iconId = R.drawable.ic_theme,
                    titleId = R.string.theme,
                    valueId = userSettings.theme.getTitleId(),
                )
            }
            item {
                CurrencyCard(
                    selectedUnit = userSettings.currency,
                    onAction = {
                        onAction(SettingsAction.UpdateCurrencyUnit(it))
                    }
                )
            }
        }
    }

    if (state.showLanguageDialog) {
        LanguageDialog(
            onDismissRequest = { onAction(SettingsAction.LanguageDialogToggle(false)) },
            initial = userSettings.language
        )
    }
    if (state.showThemeDialog) {
        ThemeDialog(
            onDismissRequest = { onAction(SettingsAction.ThemeDialogToggle(false)) },
            setTheme = { onAction(SettingsAction.UpdateTheme(it)) },
            selectedTheme = userSettings.theme
        )
    }
}
