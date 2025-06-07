/*
 * Copyright 2023 The Android Open Source Project
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

package com.demo.jetupdates.core.data.repository

import com.demo.jetupdates.core.database.dao.CategoryDao
import com.demo.jetupdates.core.database.dao.CategoryFtsDao
import com.demo.jetupdates.core.database.dao.ShopItemDao
import com.demo.jetupdates.core.database.dao.ShopItemFtsDao
import com.demo.jetupdates.core.database.model.PopulatedShopItem
import com.demo.jetupdates.core.database.model.asExternalModel
import com.demo.jetupdates.core.database.model.asFtsEntity
import com.demo.jetupdates.core.model.data.SearchResult
import com.demo.jetupdates.core.network.AppDispatchers.IO
import com.demo.jetupdates.core.network.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DefaultSearchContentsRepository @Inject constructor(
    private val shopItemDao: ShopItemDao,
    private val shopItemFtsDao: ShopItemFtsDao,
    private val categoryDao: CategoryDao,
    private val categoryFtsDao: CategoryFtsDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SearchContentsRepository {

    override suspend fun populateFtsData() {
        withContext(ioDispatcher) {
            shopItemFtsDao.insertAll(
                shopItemDao.getShopItems(
                    useFilterCategoryIds = false,
                    useFilterItemIds = false,
                )
                    .first()
                    .map(PopulatedShopItem::asFtsEntity),
            )
            categoryFtsDao.insertAll(categoryDao.getOneOffCategoryEntities().map { it.asFtsEntity() })
        }
    }

    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        // Surround the query by asterisks to match the query when it's in the middle of
        // a word
        val shopItemIds = shopItemFtsDao.searchAllShopItems("*$searchQuery*")
        val categoryIds = categoryFtsDao.searchAllCategories("*$searchQuery*")

        val shopItemsFlow = shopItemIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                shopItemDao.getShopItems(useFilterItemIds = true, filterItemIds = it)
            }
        val categoriesFlow = categoryIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest(categoryDao::getCategoryEntities)
        return combine(shopItemsFlow, categoriesFlow) { shopItems, categories ->
            SearchResult(
                categories = categories.map { it.asExternalModel() },
                shopItems = shopItems.map { it.asExternalModel() },
            )
        }
    }

    override fun getSearchContentsCount(): Flow<Int> =
        combine(
            shopItemFtsDao.getCount(),
            categoryFtsDao.getCount(),
        ) { shopItemCount, categoriesCount ->
            shopItemCount + categoriesCount
        }
}
