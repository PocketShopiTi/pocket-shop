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
import com.iti.pocketshop.features.address.presentation.view.AddressRoot
import com.iti.pocketshop.features.aichat.AiChatRoot
import com.iti.pocketshop.features.auth.forgetpassword.presentation.ForgotPasswordRoot
import com.iti.pocketshop.features.auth.login.presentation.LoginRoot
import com.iti.pocketshop.features.auth.otp.presentation.EmailVerificationRoot
import com.iti.pocketshop.features.auth.register.presentation.RegisterRoot
import com.iti.pocketshop.features.onboarding.presentation.OnboardingRoot
import com.iti.pocketshop.features.ordercheckout.OrderCheckoutRoot
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsRoot
import com.iti.pocketshop.features.search.presentation.navigation.SearchNavDisplay
import com.iti.pocketshop.features.settings.presentation.screen.SettingsRoot
import com.iti.pocketshop.features.splash.presention.SplashRoot
import com.iti.pocketshop.nestednavigation.NestedNavDisplay

@Composable
fun RootNavDisplay() {

    val rootBackStack = rememberNavBackStack(Route.Splash)

    fun openProductDetails(id: String) {
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
                        rootBackStack.popIfCurrentIs<Route.Register>()
                    }
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    currentRootRoute = rootBackStack.lastOrNull(),
                    navigateBack = {
                        rootBackStack.popIfCurrentIs<Route.NestedNav>()
                    },
                    logout = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    },
                    openProductDetails = { id -> openProductDetails(id) },
                    openSettings = {
                        rootBackStack.navigateSingleTop(Route.Settings)
                    },
                    openAddresses = {
                        rootBackStack.navigateSingleTop(Route.Address)
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
                        rootBackStack.popIfCurrentIs<Route.ProductDetails>()
                    }
                )
            }
            entry<Route.OrderCheckout> {
                OrderCheckoutRoot()
            }
            entry<Route.Settings> {
                SettingsRoot(
                    onBack = {
                        rootBackStack.popIfCurrentIs<Route.Settings>()
                    },
                )
            }
            entry<Route.SearchNav> {
                SearchNavDisplay(
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    },
                    openProductDetails = { id -> openProductDetails(id) }
                )
            }
            entry<Route.Address> {
                AddressRoot(
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    },
                )
            }
        }
    )

    SignInDialog(
        onSignIn = {
            rootBackStack.navigateSingleTop(Route.Login)
        }
    )
}
