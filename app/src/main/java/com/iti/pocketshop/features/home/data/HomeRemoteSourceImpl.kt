package com.iti.pocketshop.features.home.data

import com.apollographql.apollo.ApolloClient
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.pocketshop.core.di.StorefrontApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.core.networkutils.safeFirestoreCall
import com.iti.pocketshop.features.home.domain.HomeRemoteSource
import com.iti.pocketshop.features.home.domain.models.HomeData
import com.iti.pocketshop.features.home.domain.models.PromotionAd
import com.iti.pocketshop.shopify.HomeQuery
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRemoteSourceImpl @Inject constructor(
    @param:StorefrontApolloClient
    private val apolloClient: ApolloClient,
    private val firestore: FirebaseFirestore,
) : HomeRemoteSource {

    override suspend fun getHomeData(
        featuredCount: Int,
        bestSellerCount: Int,
        newArrivalsCount: Int,
        brandCount: Int,
    ): PocketResult<HomeData, PocketDataError.Remote> {
        return apolloClient
            .query(
                HomeQuery(
                    featuredCount = featuredCount,
                    bestSellerCount = bestSellerCount,
                    newArrivalCount = newArrivalsCount,
                    brandCount = brandCount,
                )
            )
            .safeCall()
            .map { data ->
                data.toDomain()
            }
    }

    override suspend fun getPromotionAds(): PocketResult<List<PromotionAd>, PocketDataError.Auth> {
        return safeFirestoreCall {
            firestore.collection(ADS_COLLECTION)
                .whereEqualTo(ACTIVE_FIELD, true)
                .get()
                .await()
                .documents
                .sortedBy { document -> document.id }
                .mapNotNull { document -> document.toPromotionAdOrNull() }
        }
    }

    private companion object {
        const val ADS_COLLECTION = "ads"
        const val ACTIVE_FIELD = "active"
    }
}
