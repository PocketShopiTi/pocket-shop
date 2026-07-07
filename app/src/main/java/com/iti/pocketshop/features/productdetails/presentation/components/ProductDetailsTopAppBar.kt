package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R

import com.iti.pocketshop.core.tutorial.tutorialTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductDetailsTopAppBar(
    isFavorite: Boolean,
    favoriteEnabled: Boolean,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backDescription = stringResource(R.string.product_details_back)
    val containerColor by animateColorAsState(
        targetValue = Color.Transparent,
        label = "appBarContainer",
    )
    TopAppBar(
        modifier = modifier,
        title = { },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.semantics {
                    contentDescription =
                        backDescription
                },
            ) {
                ArrowBackIcon()
            }
        },
        actions = {
            IconButton(
                onClick = onFavoriteClick,
                enabled = favoriteEnabled,
                modifier = Modifier.tutorialTarget(2)
            ) {
                AnimatedContent(
                    targetState = isFavorite,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.55f)) togetherWith
                                (fadeOut() + scaleOut(targetScale = 0.55f))
                    },
                    label = "favoriteIcon",
                ) { favorite ->
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (favorite) R.drawable.ic_favorites_filled
                            else R.drawable.ic_favorites,
                        ),
                        contentDescription = stringResource(
                            if (favorite) R.string.product_details_remove_favorite
                            else R.string.product_details_add_favorite,
                        ),
                        tint = if (favorite) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (favorite) -8f else 0f),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}
