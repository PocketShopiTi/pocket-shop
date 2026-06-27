package com.iti.pocketshop.core.di

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val BASE_URL: String = "https://mad46-and4.myshopify.com/api/2026-04/graphql.json"

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .addHttpHeader(
                "X-Shopify-Storefront-Access-Token",
                BuildConfig.STORE_FRONT_TOKEN
            )
            .build()
    }
}