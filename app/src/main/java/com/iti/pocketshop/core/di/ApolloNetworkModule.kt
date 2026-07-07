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
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApolloNetworkModule {

    const val STOREFRONT_BASE_URL = "https://mad46-and4.myshopify.com/api/2026-04/graphql.json"
    const val ADMIN_BASE_URL = "https://mad46-and4.myshopify.com/admin/api/2024-04/graphql.json"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @StorefrontApolloClient
    fun provideStorefrontApolloClient(
        okHttpClient: OkHttpClient
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(STOREFRONT_BASE_URL)
            .okHttpClient(okHttpClient)
            .addHttpHeader(
                "X-Shopify-Storefront-Access-Token",
                BuildConfig.STORE_FRONT_TOKEN
            )
            .build()
    }

    @Provides
    @AdminApolloClient
    fun provideAdminApolloClient(
        okHttpClient: OkHttpClient
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(ADMIN_BASE_URL)
            .okHttpClient(okHttpClient)
            .addHttpHeader(
                "X-Shopify-Access-Token",
                BuildConfig.SHOPIFY_ADMIN_TOKEN
            )
            .build()
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorefrontApolloClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminApolloClient