package com.iti.pocketshop.core.di

import com.iti.pocketshop.core.utils.PermissionChecker
import com.iti.pocketshop.features.address.utils.AndroidPermissionChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {

    @Binds
    @Singleton
    abstract fun bindPermissionChecker(impl: AndroidPermissionChecker): PermissionChecker
}