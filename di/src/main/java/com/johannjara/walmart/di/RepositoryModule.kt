package com.johannjara.walmart.di

import com.johannjara.walmart.data.repository.ProductRepositoryImpl
import com.johannjara.walmart.data.repository.SearchHistoryRepositoryImpl
import com.johannjara.walmart.domain.repository.ProductRepository
import com.johannjara.walmart.domain.repository.SearchHistoryRepository
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
    abstract fun bindSearchHistoryRepository(
        searchHistoryRepositoryImpl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
}
