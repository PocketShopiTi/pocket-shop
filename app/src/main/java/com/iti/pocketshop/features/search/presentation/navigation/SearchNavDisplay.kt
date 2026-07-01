package com.iti.pocketshop.features.search.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.iti.pocketshop.features.search.presentation.view.FiltersRoot
import com.iti.pocketshop.features.search.presentation.view.SearchRoot
import com.iti.pocketshop.features.search.presentation.viewmodel.SearchViewModel
import com.iti.pocketshop.rootnavigation.Route
import com.iti.pocketshop.rootnavigation.navigateSingleTop
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun SearchNavDisplay(
    onBack: () -> Unit,
    openProductDetails: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.SearchNav.Search::class, Route.SearchNav.Search.serializer())
                    subclass(Route.SearchNav.Filters::class, Route.SearchNav.Filters.serializer())
                }
            }
        },
        Route.SearchNav.Search
    )

    val handleBack: () -> Unit = {
        if (searchBackStack.size > 1) {
            searchBackStack.removeLastOrNull()
        } else {
            onBack()
        }
    }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = searchBackStack,
        onBack = { handleBack() },
        transitionSpec = {
            fadeIn(tween(350)) togetherWith fadeOut(tween(350))
        },
        entryProvider = entryProvider {
            entry<Route.SearchNav.Search> {
                SearchRoot(
                    viewModel = viewModel,
                    onBack = handleBack,
                    openProductDetails = openProductDetails,
                    onOpenFiltersScreen = {
                        searchBackStack.navigateSingleTop(Route.SearchNav.Filters)
                    }
                )
            }
            entry<Route.SearchNav.Filters> {
                FiltersRoot(
                    viewModel = viewModel,
                    onBack = {
                        searchBackStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}