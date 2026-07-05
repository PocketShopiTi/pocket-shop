package com.iti.pocketshop.core.notification

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit


class NotificationPermissionManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun requestPermissionIfNeeded(
        activity: Activity,
        requestCode: Int = DEFAULT_REQUEST_CODE,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (preferences.getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)) return

        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            preferences.edit {
                putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, true)
            }
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                requestCode,
            )
        }
    }

    private companion object {
        const val PREFS_NAME = "notification_permissions"
        const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
        const val DEFAULT_REQUEST_CODE = 2026
    }
}
