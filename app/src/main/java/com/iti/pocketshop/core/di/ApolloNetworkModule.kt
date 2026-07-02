package com.iti.pocketshop.core.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.iti.pocketshop.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApolloNetworkModule {

    const val BASE_URL: String = "https://mad46-and4.myshopify.com/api/2026-04/graphql.json"

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .addHttpHeader(
                "X-Shopify-Storefront-Access-Token",
                BuildConfig.STORE_FRONT_TOKEN
            )
            .build()
    }
}
