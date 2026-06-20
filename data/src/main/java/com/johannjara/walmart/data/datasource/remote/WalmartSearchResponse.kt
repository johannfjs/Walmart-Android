package com.johannjara.walmart.data.datasource.remote

import com.google.gson.annotations.SerializedName

data class WalmartSearchResponse(
    @SerializedName("item") val item: WalmartSearchItemContainer? = null
)

data class WalmartSearchItemContainer(
    @SerializedName("props") val props: WalmartSearchProps? = null
)

data class WalmartSearchProps(
    @SerializedName("pageProps") val pageProps: WalmartSearchPageProps? = null
)

data class WalmartSearchPageProps(
    @SerializedName("initialData") val initialData: WalmartInitialData? = null
)

data class WalmartInitialData(
    @SerializedName("searchResult") val searchResult: WalmartSearchResult? = null
)

data class WalmartSearchResult(
    @SerializedName("itemStacks") val itemStacks: List<WalmartItemStack>? = null
)

data class WalmartItemStack(
    @SerializedName("items") val items: List<WalmartProductDto>? = null
)

data class WalmartProductDto(
    @SerializedName("usItemId") val usItemId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("imageInfo") val imageInfo: WalmartImageInfo? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("priceInfo") val priceInfo: WalmartPriceInfo? = null
)

data class WalmartImageInfo(
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null
)

data class WalmartPriceInfo(
    @SerializedName("linePriceDisplay") val linePriceDisplay: String? = null
)
