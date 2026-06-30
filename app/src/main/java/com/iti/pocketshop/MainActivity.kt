package com.iti.pocketshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.core.components.ErrorDialogListener
import com.iti.pocketshop.rootnavigation.RootNavDisplay
import com.iti.pocketshop.ui.theme.PocketShopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketShopTheme {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                CompositionLocalProvider(
                    LocalUser provides currentUser
                ) {
                    RootNavDisplay()
                    ErrorDialogListener()
                }
            }
        }
    }
}
