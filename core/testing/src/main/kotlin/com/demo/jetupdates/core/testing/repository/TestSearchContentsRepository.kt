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

package com.demo.jetupdates.core.testing.repository

import com.demo.jetupdates.core.data.repository.SearchContentsRepository
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.SearchResult
import com.demo.jetupdates.core.model.data.ShopItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestSearchContentsRepository : SearchContentsRepository {

    private val cachedCategories = MutableStateFlow(emptyList<Category>())
    private val cachedShopItems = MutableStateFlow(emptyList<ShopItem>())

    override suspend fun populateFtsData() = Unit

    override fun searchContents(searchQuery: String): Flow<SearchResult> =
        combine(cachedCategories, cachedShopItems) { categories, shopItems ->
            SearchResult(
                categories = categories.filter {
                    searchQuery in it.name || searchQuery in it.shortDescription || searchQuery in it.longDescription
                },
                shopItems = shopItems.filter {
                    searchQuery in it.description || searchQuery in it.title
                },
            )
        }

    override fun getSearchContentsCount(): Flow<Int> = combine(
        cachedCategories,
        cachedShopItems,
    ) { categories, shopItems -> categories.size + shopItems.size }

    @TestOnly
    fun addCategories(categories: List<Category>) = cachedCategories.update { it + categories }

    @TestOnly
    fun addShopItems(shopItems: List<ShopItem>) =
        cachedShopItems.update { it + shopItems }
}
