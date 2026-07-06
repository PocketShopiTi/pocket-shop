package com.iti.pocketshop.features.address.di

import com.iti.pocketshop.features.address.data.datasource.ContactsDataSource
import com.iti.pocketshop.features.address.data.datasource.ContactsDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AddressContactsModule {

    @Binds
    @Singleton
    abstract fun bindContactsDataSource(impl: ContactsDataSourceImpl): ContactsDataSource


}