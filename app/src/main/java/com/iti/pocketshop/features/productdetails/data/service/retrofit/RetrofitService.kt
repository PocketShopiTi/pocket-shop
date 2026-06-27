package com.iti.pocketshop.features.productdetails.data.service.retrofit

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.iti.pocketshop.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ProductDetailsNetworkModule {

    @Provides
    @Singleton
    fun provideProductOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Access-Token", BuildConfig.ADMIN_TOKEN)
                .build()
            chain.proceed(request)
        }
        .build()

    @Provides
    @Singleton
    fun provideProductsService(client: OkHttpClient): ProductsService {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        return Retrofit.Builder()
            .baseUrl("https://mad46-and4.myshopify.com/admin/api/2026-04/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ProductsService::class.java)
    }
}
