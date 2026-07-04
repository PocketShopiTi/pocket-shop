package com.iti.pocketshop.features.onboardingnotification.data.di

import com.iti.pocketshop.features.onboardingnotification.data.datasource.NotificationAdFirestoreDataSource
import com.iti.pocketshop.features.onboardingnotification.data.datasource.NotificationAdRemoteDataSource
import com.iti.pocketshop.features.onboardingnotification.data.repository.NotificationAdRepositoryImpl
import com.iti.pocketshop.features.onboardingnotification.domain.repository.NotificationAdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingNotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationAdRemoteDataSource(
        impl: NotificationAdFirestoreDataSource,
    ): NotificationAdRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindNotificationAdRepository(
        impl: NotificationAdRepositoryImpl,
    ): NotificationAdRepository
}
