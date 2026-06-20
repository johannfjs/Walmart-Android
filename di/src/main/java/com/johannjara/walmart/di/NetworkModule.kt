package com.johannjara.walmart.di

import com.johannjara.walmart.di.BuildConfig
import com.johannjara.walmart.data.datasource.remote.NetworkConfig
import com.johannjara.walmart.data.datasource.remote.WalmartApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val baseClient = NetworkConfig.createOkHttpClient()
        return baseClient.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-rapidapi-key", BuildConfig.RAPIDAPI_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return NetworkConfig.createRetrofit(okHttpClient, "https://axesso-walmart-data-service.p.rapidapi.com/")
    }

    @Provides
    @Singleton
    fun provideWalmartApiService(retrofit: Retrofit): WalmartApiService {
        return retrofit.create(WalmartApiService::class.java)
    }
}
