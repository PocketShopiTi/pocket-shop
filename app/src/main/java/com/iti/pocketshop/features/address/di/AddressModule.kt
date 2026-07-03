package com.iti.pocketshop.features.address.di

import com.iti.pocketshop.features.address.data.datasource.AddressRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.AddressRemoteDataSourceImpl
import com.iti.pocketshop.features.address.data.datasource.AddressLocationRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.AddressLocationRemoteDataSourceImpl
import com.iti.pocketshop.features.address.data.datasource.CurrentLocationDataSource
import com.iti.pocketshop.features.address.data.datasource.CurrentLocationDataSourceImpl
import com.iti.pocketshop.features.address.data.repository.AddressRepositoryImpl
import com.iti.pocketshop.features.address.data.repository.AddressRepositoryStrings
import com.iti.pocketshop.features.address.data.repository.AndroidAddressRepositoryStrings
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AddressModule {

    @Binds
    @Singleton
    abstract fun bindAddressRemoteDataSource(
        impl: AddressRemoteDataSourceImpl,
    ): AddressRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAddressLocationRemoteDataSource(
        impl: AddressLocationRemoteDataSourceImpl,
    ): AddressLocationRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCurrentLocationDataSource(
        impl: CurrentLocationDataSourceImpl,
    ): CurrentLocationDataSource

    @Binds
    @Singleton
    abstract fun bindAddressRepository(
        impl: AddressRepositoryImpl,
    ): AddressRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepositoryStrings(
        impl: AndroidAddressRepositoryStrings,
    ): AddressRepositoryStrings
}
