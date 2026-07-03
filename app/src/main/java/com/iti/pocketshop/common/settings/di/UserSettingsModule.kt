package com.iti.pocketshop.common.settings.di

import com.iti.pocketshop.common.settings.data.UserSettingsRepoImp
import com.iti.pocketshop.common.settings.domain.UserSettingsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserSettingsModule {

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepo(
        userSettingsRepoImp: UserSettingsRepoImp
    ): UserSettingsRepo

}