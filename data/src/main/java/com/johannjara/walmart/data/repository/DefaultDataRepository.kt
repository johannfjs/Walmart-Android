package com.johannjara.walmart.data.repository

import com.johannjara.walmart.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDataRepository @Inject constructor() : DataRepository {
    override val data: Flow<List<String>> = flow { emit(listOf("Android Clean Architecture")) }
}
