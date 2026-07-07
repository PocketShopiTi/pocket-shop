package com.iti.pocketshop

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iti.pocketshop.common.settings.domain.models.LanguageSetting
import com.iti.pocketshop.common.settings.domain.models.ThemeSetting
import com.iti.pocketshop.common.settings.domain.models.UserSettings
import com.iti.pocketshop.core.components.ErrorDialogListener
import com.iti.pocketshop.core.components.StatusBarBackground
import com.iti.pocketshop.core.notification.NotificationNavigation
import com.iti.pocketshop.core.notification.NotificationPermissionManager
import com.iti.pocketshop.core.notification.NotificationTopicSubscriber
import com.iti.pocketshop.rootnavigation.RootNavDisplay
import com.iti.pocketshop.ui.theme.PocketShopTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var notificationTopicSubscriber: NotificationTopicSubscriber

    @Inject
    lateinit var notificationPermissionManager: NotificationPermissionManager

    private val viewModel: MainViewModel by viewModels()
    private var mainUiState: MainUiState by mutableStateOf(MainUiState.Loading)
    private var pendingNotificationAdId: String? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        pendingNotificationAdId = intent.notificationAdId()
        notificationTopicSubscriber.subscribeToAllTopic()
        notificationPermissionManager.requestPermissionIfNeeded(this)

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
                    RootNavDisplay(
                        pendingNotificationAdId = pendingNotificationAdId,
                        onNotificationAdHandled = {
                            pendingNotificationAdId = null
                        },
                    )
                    ErrorDialogListener()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingNotificationAdId = intent.notificationAdId()
    }

    private fun Intent?.notificationAdId(): String? {
        val adId = this?.getStringExtra(NotificationNavigation.EXTRA_AD_ID)
            ?.takeIf { it.isNotBlank() }

        val type = this?.getStringExtra(NotificationNavigation.EXTRA_TYPE)
        return when {
            adId == null -> null
            type == null -> adId
            type == NotificationNavigation.TYPE_AD_ONBOARDING -> adId
            else -> null
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
