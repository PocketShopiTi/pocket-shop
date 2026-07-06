package com.iti.pocketshop.features.onboardingnotification.data.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.safeFirestoreCall
import com.iti.pocketshop.features.onboardingnotification.data.dto.NotificationAdDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationAdFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : NotificationAdRemoteDataSource {

    override suspend fun getNotificationAd(
        adId: String,
    ): PocketResult<NotificationAdDto?, PocketDataError.Auth> =
        safeFirestoreCall {
            firestore.collection(ADS_COLLECTION)
                .document(adId)
                .get()
                .await()
                .toDto()
        }

    private fun DocumentSnapshot.toDto(): NotificationAdDto? {
        if (!exists()) return null

        return NotificationAdDto(
            id = id,
            title = getString(TITLE).orEmpty(),
            description = getString(DESCRIPTION).orEmpty(),
            couponCode = getString(COUPON_CODE).orEmpty(),
            buttonText = getString(BUTTON_TEXT).orEmpty(),
            imageUrl = getString(IMAGE_URL).orEmpty(),
            active = getBoolean(ACTIVE)
                ?: (getString(ACTIVE)?.equals("true", ignoreCase = true) == true),
        )
    }

    private companion object {
        const val ADS_COLLECTION = "ads"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val COUPON_CODE = "couponCode"
        const val BUTTON_TEXT = "buttonText"
        const val IMAGE_URL = "imageUrl"
        const val ACTIVE = "active"
    }
}
