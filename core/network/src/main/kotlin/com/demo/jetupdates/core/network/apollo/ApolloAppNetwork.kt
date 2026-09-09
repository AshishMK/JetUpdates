package com.demo.jetupdates.core.network.apollo

import androidx.tracing.trace
import com.apollographql.apollo.ApolloClient
import com.demo.jetupdates.core.network.AppNetworkDataSource
import com.demo.jetupdates.core.network.GetCategoriesQuery
import com.demo.jetupdates.core.network.GetShopItemsQuery
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem
import com.demo.jetupdates.core.network.model.asNetworkCategory
import com.demo.jetupdates.core.network.model.asNetworkShopItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ApolloClient] backed [AppNetworkDataSource]
 */
@Singleton
internal class ApolloAppNetwork @Inject constructor(
    private val apolloClient: ApolloClient,
) : AppNetworkDataSource {

    override suspend fun getCategories(ids: List<String>?): List<NetworkCategory> =
        trace("ApolloAppNetwork:getCategories") {
            val response = apolloClient.query(GetCategoriesQuery(filterIds = ids)).execute()
            response.data?.categories?.map { it.asNetworkCategory() } ?: emptyList()
        }

    override suspend fun getShopItems(ids: List<String>?): List<NetworkShopItem> =
        trace("ApolloAppNetwork:getShopItems") {
            val response = apolloClient.query(GetShopItemsQuery(filterIds = ids)).execute()
            response.data?.shopItems?.map { it.asNetworkShopItem() } ?: emptyList()
        }

    override suspend fun getCategoryChangeList(after: Int?): List<NetworkChangeList> {
        TODO("Not yet implemented")
    }

    override suspend fun getShopItemChangeList(after: Int?): List<NetworkChangeList> {
        TODO("Not yet implemented")
    }
}