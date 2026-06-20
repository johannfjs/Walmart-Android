package com.johannjara.walmart.di

import com.johannjara.walmart.data.repository.DefaultDataRepository
import com.johannjara.walmart.domain.repository.DataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDataRepository(
        defaultDataRepository: DefaultDataRepository
    ): DataRepository
}
