package com.iti.pocketshop.features.profile.di

import com.iti.pocketshop.features.profile.data.ProfileRepositoryImpl
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository
    
}