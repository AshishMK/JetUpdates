/*
 * Copyright 2026 The Android Open Source Project
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

package com.demo.jetupdates.core.network.apollo

import android.util.Log
import androidx.tracing.trace
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.network.http.LoggingInterceptor
import com.apollographql.apollo.network.okHttpClient
import com.demo.jetupdates.core.network.AppNetworkDataSource
import com.demo.jetupdates.core.network.BuildConfig.HYGRAPH_ENDPOINT
import com.demo.jetupdates.core.network.BuildConfig.HYGRAPH_TOKEN
import com.demo.jetupdates.core.network.GetCategoriesByIdsQuery
import com.demo.jetupdates.core.network.GetCategoryChangeListQuery
import com.demo.jetupdates.core.network.GetShopItemChangeListQuery
import com.demo.jetupdates.core.network.GetShopItemsByIdsQuery
import com.demo.jetupdates.core.network.apollo.GraphQLExtensions.asNetworkCategory
import com.demo.jetupdates.core.network.apollo.GraphQLExtensions.asNetworkChangeList
import com.demo.jetupdates.core.network.apollo.GraphQLExtensions.asNetworkShopItem
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem
import okhttp3.Call
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

/**
 * [ApolloClient] backed [AppNetworkDataSource]
 */
@Singleton
internal class ApolloAppNetwork @Inject constructor(
    private val okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : AppNetworkDataSource {

    private val apolloClient: ApolloClient by lazy {
        trace("ApolloAppNetwork") {
            ApolloClient.Builder()
                .serverUrl(HYGRAPH_ENDPOINT)
                // Use callFactory with dagger.Lazy<Call.Factory> to avoid main-thread OkHttp init
                .okHttpClient(okhttpCallFactory.get() as OkHttpClient)
                .addHttpHeader("Authorization", HYGRAPH_TOKEN) // or your token format
                .addHttpInterceptor(
                    LoggingInterceptor(
                        log = { message -> Log.d("ApolloNetwork", "ApolloNetwork $message") },
                    ),
                )
                .build()
        }
    }

    override suspend fun getCategories(ids: List<String>?): List<NetworkCategory> =
        trace("ApolloAppNetwork:getCategories") {
            val response = apolloClient.query(GetCategoriesByIdsQuery(ids = Optional.presentIfNotNull(ids))).execute()
            response.data?.categories?.map { it.asNetworkCategory() } ?: emptyList()
        }

    override suspend fun getShopItems(ids: List<String>?): List<NetworkShopItem> =
        trace("ApolloAppNetwork:getShopItems") {
            val response = apolloClient.query(GetShopItemsByIdsQuery(ids = Optional.presentIfNotNull(ids))).execute()
            response.data?.shopItems?.map { it.asNetworkShopItem() } ?: emptyList()
        }

    override suspend fun getCategoryChangeList(after: String?): List<NetworkChangeList> {
        val response = apolloClient.query(GetCategoryChangeListQuery(Optional.presentIfNotNull(if (after == "-1") "1970-01-01T00:00:00Z" else after))).execute() // ,Optional.presentIfNotNull(100))).execute()
        return response.data?.categories?.map { it.asNetworkChangeList() } ?: emptyList()
    }

    override suspend fun getShopItemChangeList(after: String?): List<NetworkChangeList> {
        val response = apolloClient.query(GetShopItemChangeListQuery(Optional.presentIfNotNull(if (after == "-1") "1970-01-01T00:00:00Z" else after), Optional.presentIfNotNull(100))).execute()
        // val response = apolloClient.query(GetShopItemChangeListQuery(Optional.presentIfNotNull(after), Optional.presentIfNotNull(300))).execute()
        return response.data?.shopItems?.map { it.asNetworkChangeList() } ?: emptyList()
    }
}
