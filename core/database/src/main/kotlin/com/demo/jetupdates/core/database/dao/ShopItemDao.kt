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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.demo.jetupdates.core.database.model.PopulatedShopItem
import com.demo.jetupdates.core.database.model.ShopItemCategoryCrossRef
import com.demo.jetupdates.core.database.model.ShopItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [ShopItem] and [ShopItemEntity] access
 */
@Dao
interface ShopItemDao {

    /**
     * Fetches shop items that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM shop_items
            WHERE 
                CASE WHEN :useFilterItemIds
                    THEN id IN (:filterItemIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterCategoryIds
                    THEN id IN
                        (
                            SELECT shop_item_id FROM shop_items_categories
                            WHERE category_id IN (:filterCategoryIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getShopItems(
        useFilterCategoryIds: Boolean = false,
        filterCategoryIds: Set<Int> = emptySet(),
        useFilterItemIds: Boolean = false,
        filterItemIds: Set<Int> = emptySet(),
    ): Flow<List<PopulatedShopItem>>

    /**
     * Fetches ids of shop items that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT id FROM shop_items
            WHERE 
                CASE WHEN :useFilterItemIds
                    THEN id IN (:filterItemIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterCategoryIds
                    THEN id IN
                        (
                            SELECT shop_item_id FROM shop_items_categories
                            WHERE category_id IN (:filterCategoryIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getShopItemIds(
        useFilterCategoryIds: Boolean = false,
        filterCategoryIds: Set<Int> = emptySet(),
        useFilterItemIds: Boolean = false,
        filterItemIds: Set<Int> = emptySet(),
    ): Flow<List<Int>>

    /**
     * Inserts or updates [shopItemEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertShopItems(shopItemEntities: List<ShopItemEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCategoryCrossRefEntities(
        shopItemCategoryCrossReferences: List<ShopItemCategoryCrossRef>,
    )

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(
        value = """
            DELETE FROM shop_items
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteShopItems(ids: List<Int>)
}
