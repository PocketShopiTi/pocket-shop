package com.iti.pocketshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.iti.pocketshop.core.components.ErrorDialogListener
import com.iti.pocketshop.rootnavigation.RootNavDisplay
import com.iti.pocketshop.ui.theme.PocketShopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketShopTheme {
                RootNavDisplay()
                ErrorDialogListener()
            }
        }
    }
}
