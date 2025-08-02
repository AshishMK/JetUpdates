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

import com.demo.jetupdates.core.database.dao.ShopItemDao
import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.PopulatedShopItem
import com.demo.jetupdates.core.database.model.ShopItemCategoryCrossRef
import com.demo.jetupdates.core.database.model.ShopItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.collections.map

val filteredInterestsIds = setOf(1)
val nonPresentInterestsIds = setOf(2)

/**
 * Test double for [ShopItemeDao]
 */
class TestShopItemDao : ShopItemDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<ShopItemEntity>())

    internal var categoryCrossReferences: List<ShopItemCategoryCrossRef> = emptyList()

    override fun getShopItems(
        useFilterCategoryIds: Boolean,
        filterCategoryIds: Set<Int>,
        useFilterItemIds: Boolean,
        filterItemIds: Set<Int>,
    ): Flow<List<PopulatedShopItem>> =
        entitiesStateFlow
            .map { shopItemEntities ->
                shopItemEntities.map { entity ->
                    entity.asPopulatedShopItem(categoryCrossReferences)
                }
            }
            .map { resources ->
                var result = resources
                if (useFilterCategoryIds) {
                    result = result.filter { resource ->
                        resource.categories.any { it.id in filterCategoryIds }
                    }
                }
                if (useFilterItemIds) {
                    result = result.filter { resource ->
                        resource.entity.id in filterItemIds
                    }
                }
                result
            }

    override fun getShopItemIds(
        useFilterCategoryIds: Boolean,
        filterCategoryIds: Set<Int>,
        useFilterItemIds: Boolean,
        filterItemIds: Set<Int>,
    ): Flow<List<Int>> =
        entitiesStateFlow
            .map { shopItemEntities ->
                shopItemEntities.map { entity ->
                    entity.asPopulatedShopItem(categoryCrossReferences)
                }
            }
            .map { resources ->
                var result = resources
                if (useFilterCategoryIds) {
                    result = result.filter { resource ->
                        resource.categories.any { it.id in filterCategoryIds }
                    }
                }
                if (useFilterItemIds) {
                    result = result.filter { resource ->
                        resource.entity.id in filterItemIds
                    }
                }
                result.map { it.entity.id }
            }

    override suspend fun upsertShopItems(shopItemEntities: List<ShopItemEntity>) {
        entitiesStateFlow.update { oldValues ->
            // New values come first so they overwrite old values
            (shopItemEntities + oldValues)
                .distinctBy(ShopItemEntity::id)
                .sortedWith(
                    compareBy(ShopItemEntity::publishDate).reversed(),
                )
        }
    }

    override suspend fun insertOrIgnoreCategoryCrossRefEntities(
        shopItemCategoryCrossReferences: List<ShopItemCategoryCrossRef>,
    ) {
        // Keep old values over new ones
        categoryCrossReferences = (categoryCrossReferences + shopItemCategoryCrossReferences)
            .distinctBy { it.shopItemId to it.categoryId }
    }

    override suspend fun deleteShopItems(ids: List<Int>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities ->
            entities.filterNot { it.id in idSet }
        }
    }

    override fun getShopItem(filterItemId: Int): Flow<PopulatedShopItem> = entitiesStateFlow
        .map { shopItemEntities ->
            shopItemEntities.map { entity ->
                entity.asPopulatedShopItem(categoryCrossReferences)
            }
        }
        .map { resources ->
            var result = resources.first { it.entity.id == filterItemId }

            result
        }
}

private fun ShopItemEntity.asPopulatedShopItem(
    categoryCrossReferences: List<ShopItemCategoryCrossRef>,
) = PopulatedShopItem(
    entity = this,
    categories = categoryCrossReferences
        .filter { it.shopItemId == id }
        .map { shopItemCategoryCrossRef ->
            CategoryEntity(
                id = shopItemCategoryCrossRef.categoryId,
                name = "name",
                shortDescription = "short description",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            )
        },
)
