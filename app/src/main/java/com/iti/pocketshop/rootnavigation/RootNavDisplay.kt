package com.iti.pocketshop.rootnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.iti.pocketshop.core.components.SignInDialog
import com.iti.pocketshop.features.aichat.AiChatRoot
import com.iti.pocketshop.features.auth.forgetpassword.presentation.ForgotPasswordRoot
import com.iti.pocketshop.features.auth.login.presentation.LoginRoot
import com.iti.pocketshop.features.auth.otp.presentation.EmailVerificationRoot
import com.iti.pocketshop.features.auth.register.presentation.RegisterRoot
import com.iti.pocketshop.features.onboarding.presentation.OnboardingRoot
import com.iti.pocketshop.features.ordercheckout.OrderCheckoutRoot
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsRoot
import com.iti.pocketshop.features.search.SearchRoot
import com.iti.pocketshop.features.settings.SettingsRoot
import com.iti.pocketshop.features.splash.presention.SplashRoot
import com.iti.pocketshop.nestednavigation.NestedNavDisplay

@Composable
fun RootNavDisplay() {

    val rootBackStack = rememberNavBackStack(Route.Splash)

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
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
        popTransitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        },
        entryProvider = entryProvider {
            entry<Route.Splash> {
                SplashRoot(
                    showNextScreen = { nextScreen ->
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(nextScreen)
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
                    openVerification = {
                        rootBackStack.apply {
                            navigateSingleTop(Route.EmailVerification)
                        }
                    },
                    openRegister = {
                        rootBackStack.apply {
                            navigateSingleTop(Route.Register)
                        }
                    },
                    openForgotPassword = {
                        rootBackStack.navigateSingleTop(Route.ForgotPassword)
                    }
                )
            }
            entry<Route.EmailVerification> {
                EmailVerificationRoot(
                    openLogin = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    }
                )
            }
            entry<Route.ForgotPassword> {
                ForgotPasswordRoot(
                    navigateBack = { rootBackStack.removeLastOrNull() }
                )
            }
            entry<Route.Register> {
                RegisterRoot(
                    openVerification = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.EmailVerification)
                        }
                    },
                    openLogin = {
                        rootBackStack.apply {
                            navigateSingleTop(Route.Login)
                            rootBackStack.remove(Route.Register)
                        }
                    },
                    navigateBack = {
                        rootBackStack.removeLastOrNull()
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
                    openLogin = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    },
                    openRegister = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Register)
                        }
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

    SignInDialog(
        onSignIn = {
            rootBackStack.navigateSingleTop(Route.Login)
        }
    )
}
