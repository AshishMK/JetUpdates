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

package com.demo.jetupdates.core.data.testdoubles

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.demo.jetupdates.core.network.AppNetworkDataSource
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
import okhttp3.mockwebserver.MockWebServer

/**
 * Test double for [AppNetworkDataSource]
 */
class TestApolloDataSource(val mockWebServer: MockWebServer) : AppNetworkDataSource {

    val apolloClient = ApolloClient.Builder()
        .serverUrl(mockWebServer.url("/").toString())
        .build()

    override suspend fun getCategories(ids: List<String>?): List<NetworkCategory> {
        val response = apolloClient.query(GetCategoriesByIdsQuery(ids = Optional.presentIfNotNull(ids))).execute()
        println("jklia bf ukeke $ids $response")
        return response.data?.categories?.map { it.asNetworkCategory() } ?: emptyList()
    }

    override suspend fun getShopItems(ids: List<String>?): List<NetworkShopItem> {
        val response = apolloClient.query(GetShopItemsByIdsQuery(ids = Optional.presentIfNotNull(ids))).execute()
        return response.data?.shopItems?.map { it.asNetworkShopItem() } ?: emptyList()
    }

    override suspend fun getCategoryChangeList(after: String?): List<NetworkChangeList> {
        val response = apolloClient.query(GetCategoryChangeListQuery(Optional.presentIfNotNull(after))).execute()
        return response.data?.categories?.map { it.asNetworkChangeList() } ?: emptyList()
    }

    override suspend fun getShopItemChangeList(after: String?): List<NetworkChangeList> {
        val response = apolloClient.query(GetShopItemChangeListQuery(Optional.presentIfNotNull(if (after == "-1") "1970-01-01T00:00:00Z" else after), Optional.presentIfNotNull(100))).execute()
        return response.data?.shopItems?.map { it.asNetworkChangeList() } ?: emptyList()
    }
}
