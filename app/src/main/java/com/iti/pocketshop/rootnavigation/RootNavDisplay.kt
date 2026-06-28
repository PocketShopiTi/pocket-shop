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
import com.iti.pocketshop.features.login.LoginRoot
import com.iti.pocketshop.features.onboarding.OnboardingRoot
import com.iti.pocketshop.features.ordercheckout.OrderCheckoutRoot
import com.iti.pocketshop.features.otp.OTPRoot
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsRoot
import com.iti.pocketshop.features.register.RegisterRoot
import com.iti.pocketshop.features.search.SearchRoot
import com.iti.pocketshop.features.settings.SettingsRoot
import com.iti.pocketshop.nestednavigation.NestedNavDisplay
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun RootNavDisplay() {

    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Onboarding::class, Route.Onboarding.serializer())
                    subclass(Route.Login::class, Route.Login.serializer())
                    subclass(Route.OTP::class, Route.OTP.serializer())
                    subclass(Route.Register::class, Route.Register.serializer())
                    subclass(Route.NestedNav::class, Route.NestedNav.serializer())
                    subclass(Route.ProductDetails::class, Route.ProductDetails.serializer())
                    subclass(Route.AiChat::class, Route.AiChat.serializer())
                    subclass(Route.OrderCheckout::class, Route.OrderCheckout.serializer())
                    subclass(Route.Settings::class, Route.Settings.serializer())
                    subclass(Route.Search::class, Route.Search.serializer())
                }
            }
        },
        Route.Onboarding
    )

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = rootBackStack,
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
                    openOTP = {
                        rootBackStack.apply {
                            navigateSingleTop(Route.OTP)
                        }
                    },
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
                    openServiceOrder = { id ->
                        rootBackStack.navigateSingleTop(Route.ProductDetails(id = id))
                    },
                    openSettings = {
                        rootBackStack.navigateSingleTop(Route.Settings)
                    },
                    openSearch = {
                        rootBackStack.navigateSingleTop(Route.Search)
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
            entry<Route.Search> {
                SearchRoot()
            }
        }
    )
}