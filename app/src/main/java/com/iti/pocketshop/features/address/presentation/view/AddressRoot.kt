package com.iti.pocketshop.features.address.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.iti.pocketshop.features.address.domain.error.toUiMessage
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.viewmodel.AddressViewModel

@Composable
fun AddressRoot(
    onBack: () -> Unit,
    viewModel: AddressViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var hasRequestedLocationPermission by rememberSaveable(
        state.editor.visible,
        state.editor.isEditing,
    ) {
        mutableStateOf(false)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onAction(AddressAction.LocationPermissionResult(granted))
    }

    LaunchedEffect(state.editor.visible, state.editor.isEditing, state.editor.latitude, state.editor.longitude) {
        val hasSavedLocation = state.editor.latitude != null && state.editor.longitude != null
        val shouldRequestPermission = state.editor.visible && !state.editor.isEditing && !hasSavedLocation
        if (shouldRequestPermission && !hasRequestedLocationPermission) {
            hasRequestedLocationPermission = true
            val permissionGranted = hasLocationPermission(context)

            if (permissionGranted) {
                viewModel.onAction(AddressAction.LocationPermissionResult(true))
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if (!state.editor.visible) {
            hasRequestedLocationPermission = false
        }
    }

    BackHandler(enabled = state.editor.visible) {
        viewModel.onAction(AddressAction.CloseEditor)
    }

    AddressScreen(
        state = state,
        errorMessage = state.error?.toUiMessage(context),
        onAction = viewModel::onAction,
        onBack = {
            if (state.editor.visible) {
                viewModel.onAction(AddressAction.CloseEditor)
            } else {
                onBack()
            }
        },
    )
}

private fun hasLocationPermission(context: android.content.Context): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    return fineGranted || coarseGranted
}