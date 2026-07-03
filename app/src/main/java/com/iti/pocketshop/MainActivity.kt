package com.iti.pocketshop

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iti.pocketshop.core.components.ErrorDialogListener
import com.iti.pocketshop.common.settings.domain.models.LanguageSetting
import com.iti.pocketshop.common.settings.domain.models.ThemeSetting
import com.iti.pocketshop.common.settings.domain.models.UserSettings
import com.iti.pocketshop.rootnavigation.RootNavDisplay
import com.iti.pocketshop.ui.theme.PocketShopTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var mainUiState: MainUiState by mutableStateOf(MainUiState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            mainUiState == MainUiState.Loading
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainUiState.collect {
                    mainUiState = it
                }
            }
        }

        lifecycleScope.launch {
            val locales = AppCompatDelegate.getApplicationLocales()
            val langTag = locales.toLanguageTags()
            val languageSetting = when (langTag) {
                "ar" -> LanguageSetting.ARABIC
                else -> LanguageSetting.ENGLISH
            }
            viewModel.saveLanguageSettings(languageSetting)
        }

        enableEdgeToEdge()
        setContent {
            val isDarkTheme = shouldShowDarkTheme(mainUiState)
            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme) SystemBarStyle.dark(Color.TRANSPARENT)
                    else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                )
            }

            PocketShopTheme(
                isDarkTheme = isDarkTheme
            ) {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                CompositionLocalProvider(
                    LocalUser provides currentUser,
                    LocalSettingsUser provides ((mainUiState as? MainUiState.Ready)?.userSettings
                        ?: UserSettings())
                ) {
                    RootNavDisplay()
                    ErrorDialogListener()
                }
            }
        }
    }
}

@Composable
fun shouldShowDarkTheme(
    uiState: MainUiState
): Boolean = when (uiState) {
    MainUiState.Loading -> isSystemInDarkTheme()
    is MainUiState.Ready ->
        when (uiState.userSettings.theme) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        }
}