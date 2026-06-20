package com.johannjara.walmart.data.repository

import com.johannjara.walmart.data.datasource.local.ProductCacheDao
import com.johannjara.walmart.data.datasource.remote.WalmartApiService
import com.johannjara.walmart.data.entity.ProductCacheEntity
import com.johannjara.walmart.domain.model.Product
import com.johannjara.walmart.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: WalmartApiService,
    private val productCacheDao: ProductCacheDao
) : ProductRepository {

    private var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    constructor(
        apiService: WalmartApiService,
        productCacheDao: ProductCacheDao,
        ioDispatcher: CoroutineDispatcher
    ) : this(apiService, productCacheDao) {
        this.ioDispatcher = ioDispatcher
    }

    override fun getCachedProductsStream(keyword: String): Flow<List<Product>> {
        return productCacheDao.getCachedProductsStream(keyword).map { list ->
            list.map { entity ->
                Product(
                    id = entity.id,
                    title = entity.title,
                    price = entity.price,
                    imageUrl = entity.thumbnailUrl
                )
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun fetchAndCacheNextPage(keyword: String, page: Int): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (page == 1) {
                productCacheDao.deleteProductsByKeyword(keyword)
            }

            val response = apiService.searchProducts(keyword, page)
            val itemStacks = response.item?.props?.pageProps?.initialData?.searchResult?.itemStacks ?: emptyList()
            val items = itemStacks.flatMap { it.items ?: emptyList() }

            val entities = items
                .filter { !it.usItemId.isNullOrBlank() && !it.name.isNullOrBlank() }
                .map { dto ->
                    ProductCacheEntity(
                        id = dto.usItemId!!,
                        searchKeyword = keyword,
                        title = dto.name!!,
                        price = dto.price ?: 0.0,
                        thumbnailUrl = dto.imageInfo?.thumbnailUrl ?: dto.image ?: "",
                        cachedAtPage = page
                    )
                }

            productCacheDao.insertProducts(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
