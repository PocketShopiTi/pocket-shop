package com.iti.pocketshop.features.register.di

import com.iti.pocketshop.features.register.data.FirebaseAuthRepositoryImpl
import com.iti.pocketshop.features.register.domain.repo.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RegisterDi {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): AuthRepository
}
