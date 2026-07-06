package com.iti.pocketshop.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.iti.pocketshop.network.BuildConfig
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