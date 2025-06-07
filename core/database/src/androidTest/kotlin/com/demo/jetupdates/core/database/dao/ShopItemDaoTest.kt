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

package com.demo.jetupdates.core.database.dao

import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.ShopItemCategoryCrossRef
import com.demo.jetupdates.core.database.model.ShopItemEntity
import com.demo.jetupdates.core.database.model.asExternalModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

internal class ShopItemDaoTest : DatabaseTest() {

    @Test
    fun getShopItems_allEntries_areOrderedByPublishDateDesc() = runTest {
        val shopItemEntities = listOf(
            testShopItem(
                id = 0,
                millisSinceEpoch = 0,
            ),
            testShopItem(
                id = 1,
                millisSinceEpoch = 3,
            ),
            testShopItem(
                id = 2,
                millisSinceEpoch = 1,
            ),
            testShopItem(
                id = 3,
                millisSinceEpoch = 2,
            ),
        )
        shopItemDao.upsertShopItems(
            shopItemEntities,
        )

        val savedShopItemEntities = shopItemDao.getShopItems()
            .first()

        assertEquals(
            listOf(3L, 2L, 1L, 0L),
            savedShopItemEntities.map {
                it.asExternalModel().publishDate.toEpochMilliseconds()
            },
        )
    }

    @Test
    fun getShopItems_filteredById_areOrderedByDescendingPublishDate() = runTest {
        val shopItemEntities = listOf(
            testShopItem(
                id = 0,
                millisSinceEpoch = 0,
            ),
            testShopItem(
                id = 1,
                millisSinceEpoch = 3,
            ),
            testShopItem(
                id = 2,
                millisSinceEpoch = 1,
            ),
            testShopItem(
                id = 3,
                millisSinceEpoch = 2,
            ),
        )
        shopItemDao.upsertShopItems(
            shopItemEntities,
        )

        val savedShopItemEntities = shopItemDao.getShopItems(
            useFilterItemIds = true,
            filterItemIds = setOf(3, 0),
        )
            .first()

        assertEquals(
            listOf(3, 0),
            savedShopItemEntities.map {
                it.entity.id
            },
        )
    }

    @Test
    fun getShopItems_filteredByCategoryId_areOrderedByDescendingPublishDate() = runTest {
        val categoryEntities = listOf(
            testCategoryEntity(
                id = 1,
                name = "1",
            ),
            testCategoryEntity(
                id = 2,
                name = "2",
            ),
        )
        val shopItemEntities = listOf(
            testShopItem(
                id = 0,
                millisSinceEpoch = 0,
            ),
            testShopItem(
                id = 1,
                millisSinceEpoch = 3,
            ),
            testShopItem(
                id = 2,
                millisSinceEpoch = 1,
            ),
            testShopItem(
                id = 3,
                millisSinceEpoch = 2,
            ),
        )
        val shopItemCategoryCrossRefEntities = categoryEntities.mapIndexed { index, categoryEntity ->
            ShopItemCategoryCrossRef(
                shopItemId = index,
                categoryId = categoryEntity.id,
            )
        }

        categoryDao.insertOrIgnoreCategories(
            categoryEntities = categoryEntities,
        )
        shopItemDao.upsertShopItems(
            shopItemEntities,
        )
        shopItemDao.insertOrIgnoreCategoryCrossRefEntities(
            shopItemCategoryCrossRefEntities,
        )

        val filteredShopItems = shopItemDao.getShopItems(
            useFilterCategoryIds = true,
            filterCategoryIds = categoryEntities
                .map(CategoryEntity::id)
                .toSet(),
        ).first()

        assertEquals(
            listOf(1, 0),
            filteredShopItems.map { it.entity.id },
        )
    }

    @Test
    fun getShopItems_filteredByIdAndCategoryId_areOrderedByDescendingPublishDate() = runTest {
        val categoryEntities = listOf(
            testCategoryEntity(
                id = 1,
                name = "1",
            ),
            testCategoryEntity(
                id = 2,
                name = "2",
            ),
        )
        val shopItemEntities = listOf(
            testShopItem(
                id = 0,
                millisSinceEpoch = 0,
            ),
            testShopItem(
                id = 1,
                millisSinceEpoch = 3,
            ),
            testShopItem(
                id = 2,
                millisSinceEpoch = 1,
            ),
            testShopItem(
                id = 3,
                millisSinceEpoch = 2,
            ),
        )
        val shopItemCategoryCrossRefEntities = categoryEntities.mapIndexed { index, categoryEntity ->
            ShopItemCategoryCrossRef(
                shopItemId = index,
                categoryId = categoryEntity.id,
            )
        }

        categoryDao.insertOrIgnoreCategories(
            categoryEntities = categoryEntities,
        )
        shopItemDao.upsertShopItems(
            shopItemEntities,
        )
        shopItemDao.insertOrIgnoreCategoryCrossRefEntities(
            shopItemCategoryCrossRefEntities,
        )

        val filteredShopItems = shopItemDao.getShopItems(
            useFilterCategoryIds = true,
            filterCategoryIds = categoryEntities
                .map(CategoryEntity::id)
                .toSet(),
            useFilterItemIds = true,
            filterItemIds = setOf(1),
        ).first()

        assertEquals(
            listOf(1),
            filteredShopItems.map { it.entity.id },
        )
    }

    @Test
    fun deleteShopItems_byId() =
        runTest {
            val shopItemEntities = listOf(
                testShopItem(
                    id = 0,
                    millisSinceEpoch = 0,
                ),
                testShopItem(
                    id = 1,
                    millisSinceEpoch = 3,
                ),
                testShopItem(
                    id = 2,
                    millisSinceEpoch = 1,
                ),
                testShopItem(
                    id = 3,
                    millisSinceEpoch = 2,
                ),
            )
            shopItemDao.upsertShopItems(shopItemEntities)

            val (toDelete, toKeep) = shopItemEntities.partition { it.id.toInt() % 2 == 0 }

            shopItemDao.deleteShopItems(
                toDelete.map(ShopItemEntity::id),
            )

            assertEquals(
                toKeep.map(ShopItemEntity::id)
                    .toSet(),
                shopItemDao.getShopItems().first()
                    .map { it.entity.id }
                    .toSet(),
            )
        }
}

private fun testCategoryEntity(
    id: Int = 0,
    name: String,
) = CategoryEntity(
    id = id,
    name = name,
    shortDescription = "",
    longDescription = "",
    url = "",
    imageUrl = "",
)

private fun testShopItem(
    id: Int = 0,
    millisSinceEpoch: Long = 0,
) = ShopItemEntity(
    id = id,
    title = "",
    price = 0f,
    description = "",
    stock = 0,
    images = emptyList(),
    publishDate = Instant.fromEpochMilliseconds(millisSinceEpoch),
    type = "Article 📚",
)
