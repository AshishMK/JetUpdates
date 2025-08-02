/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.jetupdates.core.network.retrofit

import androidx.tracing.trace
import com.demo.jetupdates.core.network.AppNetworkDataSource
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for JU app Network API
 */
private interface RetrofitAppNetworkApi {
    @GET(value = "categories")
    suspend fun getCategories(
        @Query("id") ids: List<Int>?,
    ): NetworkResponse<List<NetworkCategory>>

    @GET(value = "shopitems")
    suspend fun getShopItems(
        @Query("id") ids: List<Int>?,
    ): NetworkResponse<List<NetworkShopItem>>

    @GET(value = "changelists/categories")
    suspend fun getCategoryChangeList(
        @Query("after") after: Int?,
    ): List<NetworkChangeList>

    @GET(value = "changelists/shopitems")
    suspend fun getShopItemsChangeList(
        @Query("after") after: Int?,
    ): List<NetworkChangeList>
}

private val APP_BASE_URL = "http://example.com"
// BuildConfig.BACKEND_URL

/**
 * Wrapper for data provided from the [APP_BASE_URL]
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

/**
 * [Retrofit] backed [AppNetworkDataSource]
 */
@Singleton
internal class RetrofitAppNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : AppNetworkDataSource {

    private val networkApi = trace("RetrofitAppNetwork") {
        Retrofit.Builder()
            .baseUrl(APP_BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitAppNetworkApi::class.java)
    }

    override suspend fun getCategories(ids: List<Int>?): List<NetworkCategory> =
        networkApi.getCategories(ids = ids).data

    override suspend fun getShopItems(ids: List<Int>?): List<NetworkShopItem> =
        networkApi.getShopItems(ids = ids).data

    override suspend fun getCategoryChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getCategoryChangeList(after = after)

    override suspend fun getShopItemChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getShopItemsChangeList(after = after)
}
