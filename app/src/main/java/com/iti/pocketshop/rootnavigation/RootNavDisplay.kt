package com.iti.pocketshop.rootnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.iti.pocketshop.features.onboardingnotification.presentation.OnboardingNotificationRoot
import com.iti.pocketshop.features.onboarding.presentation.OnboardingRoot
import com.iti.pocketshop.features.checkout.presentation.OrderCheckoutRoot
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsRoot
import com.iti.pocketshop.features.search.presentation.navigation.SearchNavDisplay
import com.iti.pocketshop.features.settings.presentation.screen.SettingsRoot
import com.iti.pocketshop.features.splash.presention.SplashRoot
import com.iti.pocketshop.nestednavigation.NestedNavDisplay
import com.iti.pocketshop.features.brands.presentation.BrandsRoot
import com.iti.pocketshop.features.productlist.presentation.ProductListRoot

@Composable
fun RootNavDisplay(
    pendingNotificationAdId: String? = null,
    onNotificationAdHandled: () -> Unit = {},
) {

    val rootBackStack = rememberNavBackStack(Route.Splash)

    fun openProductDetails(id: String) {
        rootBackStack.navigateSingleTop(Route.ProductDetails(id = id))
    }

    LaunchedEffect(pendingNotificationAdId) {
        val adId = pendingNotificationAdId?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        rootBackStack.navigateSingleTop(Route.OnboardingNotification(adId = adId))
        onNotificationAdHandled()
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
            entry<Route.OnboardingNotification> {
                OnboardingNotificationRoot(
                    adId = it.adId,
                    openHome = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.NestedNav)
                        }
                    },
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
                        rootBackStack.navigateSingleTop(Route.Login)
                    },
                    openRegister = {
                        rootBackStack.navigateSingleTop(Route.Register)
                    },
                    openSearch = {
                        rootBackStack.navigateSingleTop(Route.SearchNav)
                    },
                    openBrands = {
                        rootBackStack.navigateSingleTop(Route.Brands)
                    },
                    openProductList = { routeInfo ->
                        rootBackStack.navigateSingleTop(Route.ProductList(routeInfo))
                    },
                    openOrderCheckout = {
                        rootBackStack.navigateSingleTop(Route.OrderCheckout)
                    }
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
                    }
                )
            }
            entry<Route.Brands> {
                BrandsRoot(
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    },
                    onBrandClick = { brandName ->
                        rootBackStack.navigateSingleTop(Route.ProductList(brandName))
                    }
                )
            }
            entry<Route.ProductList> {
                ProductListRoot(
                    routeInfo = it.routeInfo,
                    onBack = {
                        rootBackStack.removeLastOrNull()
                    },
                    onProductClick = { id ->
                        rootBackStack.navigateSingleTop(Route.ProductDetails(id = id))
                    }
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
