package com.johannjara.walmart.data.datasource.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WalmartApiService {

    @GET("wlm/walmart-search-by-keyword")
    suspend fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("sortBy") sortBy: String = "best_match"
    ): WalmartSearchResponse
}
