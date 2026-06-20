package com.johannjara.walmart.di

import android.content.Context
import androidx.room.Room
import com.johannjara.walmart.data.datasource.local.AppDatabase
import com.johannjara.walmart.data.datasource.local.DummyDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "walmart_db"
        ).build()
    }

    @Provides
    fun provideDummyDao(appDatabase: AppDatabase): DummyDao {
        return appDatabase.dummyDao()
    }
}
