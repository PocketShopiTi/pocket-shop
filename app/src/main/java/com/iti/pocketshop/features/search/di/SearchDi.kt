package com.iti.pocketshop.features.search.di

import com.iti.pocketshop.features.search.data.datasource.SearchRemoteDataSourceImpl
import com.iti.pocketshop.features.search.data.repository.SearchRepositoryImpl
import com.iti.pocketshop.features.search.data.datasource.SearchRemoteDataSource
import com.iti.pocketshop.features.search.domain.repostory.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchDi {

    @Binds
     abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
     abstract fun bindSearchRemoteDataSource(
        searchRemoteDataSourceImpl: SearchRemoteDataSourceImpl
    ): SearchRemoteDataSource
}
