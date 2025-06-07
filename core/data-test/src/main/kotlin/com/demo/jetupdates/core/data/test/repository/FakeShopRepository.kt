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

package com.demo.jetupdates.core.data.test.repository

import com.demo.jetupdates.core.data.Synchronizer
import com.demo.jetupdates.core.data.model.asExternalModel
import com.demo.jetupdates.core.data.repository.ShopItemQuery
import com.demo.jetupdates.core.data.repository.ShopRepository
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.network.AppDispatchers.IO
import com.demo.jetupdates.core.network.Dispatcher
import com.demo.jetupdates.core.network.demo.DemoAppNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Fake implementation of the [ShopRepository] that retrieves the shop items from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeShopRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: DemoAppNetworkDataSource,
) : ShopRepository {

    override fun getShopItems(
        query: ShopItemQuery,
    ): Flow<List<ShopItem>> =
        flow {
            val shopItems = datasource.getShopItems()
            val categories = datasource.getCategories()

            emit(
                shopItems
                    .filter { networkShopItem ->
                        // Filter out any shop items which don't match the current query.
                        // If no query parameters (filterCategoryIds or filterItemIds) are specified
                        // then the shop item is returned.
                        listOfNotNull(
                            true,
                            query.filterItemIds?.contains(networkShopItem.id),
                            query.filterCategoryIds?.let { filterCategoryIds ->
                                networkShopItem.categories.intersect(filterCategoryIds).isNotEmpty()
                            },
                        )
                            .all(true::equals)
                    }
                    .map { it.asExternalModel(categories) },
            )
        }.flowOn(ioDispatcher)

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
