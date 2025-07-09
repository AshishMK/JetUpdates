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

package com.demo.jetupdates.core.testing.repository

import com.demo.jetupdates.core.data.Synchronizer
import com.demo.jetupdates.core.data.repository.ShopItemQuery
import com.demo.jetupdates.core.data.repository.ShopRepository
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.ShopItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestShopRepository : ShopRepository {

    /**
     * The backing hot flow for the list of categories ids for testing.
     */
    private val shopItemsFlow: MutableSharedFlow<List<ShopItem>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getShopItems(query: ShopItemQuery): Flow<List<ShopItem>> =
        shopItemsFlow.map { shopItems ->
            var result = shopItems
            query.filterCategoryIds?.let { filterCategoryIds ->
                result = shopItems.filter {
                    it.categories.map(Category::id).intersect(filterCategoryIds).isNotEmpty()
                }
            }
            query.filterItemIds?.let { filterItemIds ->
                result = shopItems.filter { it.id in filterItemIds }
            }
            result
        }

    override fun getShopItem(id: Int): Flow<ShopItem> =
        shopItemsFlow.map { shopItems ->
            val result = shopItems.first { it.id == id }

            result
        }

    /**
     * A test-only API to allow controlling the list of shop items from tests.
     */
    fun sendShopItems(shopItems: List<ShopItem>) {
        shopItemsFlow.tryEmit(shopItems)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
