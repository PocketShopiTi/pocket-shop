package com.iti.pocketshop.features.address.presentation.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.iti.pocketshop.R
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.view.component.AddressEditorContent
import com.iti.pocketshop.features.address.presentation.view.component.AddressListContent
import com.iti.pocketshop.features.address.presentation.view.component.AddressTopBar
import com.iti.pocketshop.features.address.presentation.view.component.DeleteConfirmDialog
import com.iti.pocketshop.features.address.presentation.state.AddressState
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
fun AddressScreen(
    state: AddressState,
    errorMessage: String?,
    onAction: (AddressAction) -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val extendedColors = LocalExtendedColors.current

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            onAction(AddressAction.DismissMessage)
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onAction(AddressAction.DismissError)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = extendedColors.background,
        topBar = {
            AddressTopBar(
                title = when {
                    state.editor.visible && state.editor.isEditing -> stringResource(R.string.address_title_edit)
                    state.editor.visible -> stringResource(R.string.address_title_add)
                    else -> stringResource(R.string.address_title_list)
                },
                showRefresh = !state.editor.visible,
                isLoading = state.isLoading,
                isSaving = state.isSaving,
                onBack = onBack,
                onRefresh = { onAction(AddressAction.Refresh) },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding(),
        ) {
            AnimatedContent(
                targetState = state.editor.visible,
                label = "address-mode",
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
            ) { editing ->
                if (editing) {
                    AddressEditorContent(
                        state = state,
                        onAction = onAction,
                    )
                } else {
                    AddressListContent(
                        state = state,
                        onAction = onAction,
                    )
                }
            }

            if (state.isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = extendedColors.primary)
                }
            }
        }
    }

    DeleteConfirmDialog(
        state = state,
        onAction = onAction,
    )
}
