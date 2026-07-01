package com.iti.pocketshop.rootnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.iti.pocketshop.features.aichat.AiChatRoot
import com.iti.pocketshop.features.login.presentation.LoginRoot
import com.iti.pocketshop.features.onboarding.presentation.OnboardingRoot
import com.iti.pocketshop.features.ordercheckout.OrderCheckoutRoot
import com.iti.pocketshop.features.otp.OTPRoot
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsRoot
import com.iti.pocketshop.features.register.presentation.view.RegisterRoot
import com.iti.pocketshop.features.search.presentation.navigation.SearchNavDisplay
import com.iti.pocketshop.features.settings.SettingsRoot
import com.iti.pocketshop.features.splash.presention.SplashRoot
import com.iti.pocketshop.nestednavigation.NestedNavDisplay
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun RootNavDisplay() {

    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Splash::class, Route.Splash.serializer())
                    subclass(Route.Onboarding::class, Route.Onboarding.serializer())
                    subclass(Route.Login::class, Route.Login.serializer())
                    subclass(Route.OTP::class, Route.OTP.serializer())
                    subclass(Route.Register::class, Route.Register.serializer())
                    subclass(Route.NestedNav::class, Route.NestedNav.serializer())
                    subclass(Route.ProductDetails::class, Route.ProductDetails.serializer())
                    subclass(Route.AiChat::class, Route.AiChat.serializer())
                    subclass(Route.OrderCheckout::class, Route.OrderCheckout.serializer())
                    subclass(Route.Settings::class, Route.Settings.serializer())
                    subclass(Route.SearchNav::class, Route.SearchNav.serializer())
                }
            }
        },
        Route.Splash
    )

    val openProductDetails: (String) -> Unit = { id ->
        rootBackStack.navigateSingleTop(Route.ProductDetails(id = id))
    }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = rootBackStack,
        onBack = {
            rootBackStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
        ),
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        entryProvider = entryProvider {
            entry<Route.Splash> {
                SplashRoot(
                    showNextScreen = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Onboarding)
                        }
                    }
                )
            }
            entry<Route.Onboarding> {
                OnboardingRoot(
                    openLogin = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    }
                )
            }
            entry<Route.Login> {
                LoginRoot(
                    openHome = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.NestedNav)
                        }
                    },
                    openOTP = {
                        rootBackStack.apply {
                            navigateSingleTop(Route.OTP)
                        }
                    },
                    openRegister = {
                        rootBackStack.apply {
                            navigateSingleTop(Route.Register)
                        }
                    },
                    openForgotPassword = {
                        // TODO: Navigate to Forgot Password Route when implemented
                    }
                )
            }
            entry<Route.OTP> {
                OTPRoot(
                    openHome = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.NestedNav)
                        }
                    },
                    openRegister = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Register)
                        }
                    }
                )
            }
            entry<Route.Register> {
                RegisterRoot(
                    openHome = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.NestedNav)
                        }
                    },
                    openLogin = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    }
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    currentRootRoute = rootBackStack.lastOrNull(),
                    navigateBack = {
                        rootBackStack.removeLastOrNull()
                    },
                    logout = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    },
                    openServiceOrder = openProductDetails,
                    openSettings = {
                        rootBackStack.navigateSingleTop(Route.Settings)
                    },
                    openSearch = {
                        rootBackStack.navigateSingleTop(Route.SearchNav)
                    },
                )
            }
            entry<Route.AiChat> {
                AiChatRoot()
            }
            entry<Route.ProductDetails> {
                ProductDetailsRoot(
                    productId = it.id,
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    }
                )
            }
            entry<Route.OrderCheckout> {
                OrderCheckoutRoot()
            }
            entry<Route.Settings> {
                SettingsRoot()
            }
            entry<Route.SearchNav> {
                SearchNavDisplay(
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    },
                    openProductDetails = openProductDetails
                )
            }
        }
    )
}