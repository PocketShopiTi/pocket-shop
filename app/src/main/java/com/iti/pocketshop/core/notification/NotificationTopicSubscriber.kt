package com.iti.pocketshop.core.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton


class NotificationTopicSubscriber @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
) {

    fun subscribeToAllTopic() {
        firebaseMessaging.subscribeToTopic(TOPIC_ALL)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Subscribed to FCM topic: $TOPIC_ALL")
                } else {
                    Log.w(TAG, "Failed to subscribe to FCM topic: $TOPIC_ALL", task.exception)
                }
            }
    }

    private companion object {
        const val TAG = "NotificationTopic"
        const val TOPIC_ALL = "all"
    }
}
