package com.iti.pocketshop.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.iti.pocketshop.MainActivity
import com.iti.pocketshop.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PocketFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var topicSubscriber: NotificationTopicSubscriber

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val type = data[NotificationNavigation.EXTRA_TYPE]
        val adId = data[NotificationNavigation.EXTRA_AD_ID].orEmpty()

        if (adId.isNotBlank() && (
                type == null ||
                type == NotificationNavigation.TYPE_AD_ONBOARDING
            )
        ) {
            showAdOnboardingNotification(adId)
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated Firebase callback", level = DeprecationLevel.WARNING)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        topicSubscriber.subscribeToAllTopic()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showAdOnboardingNotification(adId: String) {
        if (!canPostNotifications()) return

        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(NotificationNavigation.EXTRA_TYPE, NotificationNavigation.TYPE_AD_ONBOARDING)
            putExtra(NotificationNavigation.EXTRA_AD_ID, adId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            adId.notificationId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_ad_title))
            .setContentText(getString(R.string.notification_ad_body))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.notification_ad_body))
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).notify(adId.notificationId(), notification)
    }


    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_promotions),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = getString(R.string.notification_ad_body)
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "promotion_notifications"
    }
}

private fun String.notificationId(): Int = hashCode() and 0x7fffffff