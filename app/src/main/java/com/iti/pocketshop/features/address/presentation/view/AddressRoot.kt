package com.iti.pocketshop.features.address.presentation.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
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

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri = result.data?.data
            if (contactUri != null) {
                context.contentResolver.query(
                    contactUri,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ),
                    null,
                    null,
                    null,
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val number = if (numberIndex >= 0) cursor.getString(numberIndex) else null
                        val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
                        viewModel.onAction(AddressAction.ContactPicked(displayName = name, phoneNumber = number))
                    }
                }
            }
        }
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            pickContactLauncher.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI))
        }
    }

    fun launchContactPicker() {
        val hasContactsPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS,
        ) == PackageManager.PERMISSION_GRANTED

        if (hasContactsPermission) {
            pickContactLauncher.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI))
        } else {
            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
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
        onPickContact = { launchContactPicker() },
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