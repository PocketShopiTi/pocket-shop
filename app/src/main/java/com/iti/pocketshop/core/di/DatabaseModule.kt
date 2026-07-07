package com.iti.pocketshop.core.di

import android.content.Context
import androidx.room.Room
import com.iti.pocketshop.common.favorites.data.local.FavoriteDao
import com.iti.pocketshop.core.database.PocketDatabase
import com.iti.pocketshop.features.cart.data.local.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PocketDatabase {
        return Room.databaseBuilder(
            context,
            PocketDatabase::class.java,
            "pocket_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoritesDao(database: PocketDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: PocketDatabase): CartDao {
        return database.cartDao()
    }
}
