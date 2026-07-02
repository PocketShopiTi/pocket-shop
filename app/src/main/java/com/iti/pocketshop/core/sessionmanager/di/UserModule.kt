package com.iti.pocketshop.core.sessionmanager.di

import com.iti.pocketshop.core.sessionmanager.data.datasource.firebase.FirebaseUserDataSource
import com.iti.pocketshop.core.sessionmanager.data.datasource.firebase.FirebaseUserDataSourceImpl
import com.iti.pocketshop.core.sessionmanager.data.repository.UserRepoImpl
import com.iti.pocketshop.core.sessionmanager.domain.repository.UserRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    @Binds
    @Singleton
    abstract fun bindFirebaseUserDataSource(
        impl: FirebaseUserDataSourceImpl,
    ): FirebaseUserDataSource

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepoImpl,
    ): UserRepo
}