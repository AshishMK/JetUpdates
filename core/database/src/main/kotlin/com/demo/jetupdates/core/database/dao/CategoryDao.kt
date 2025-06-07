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
import androidx.room.Upsert
import com.demo.jetupdates.core.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [CategoryEntity] access
 */
@Dao
interface CategoryDao {
    @Query(
        value = """
        SELECT * FROM categories
        WHERE id = :categoryId
    """,
    )
    fun getCategoryEntity(categoryId: Int): Flow<CategoryEntity>

    @Query(value = "SELECT * FROM categories")
    fun getCategoryEntities(): Flow<List<CategoryEntity>>

    @Query(value = "SELECT * FROM categories")
    suspend fun getOneOffCategoryEntities(): List<CategoryEntity>

    @Query(
        value = """
        SELECT * FROM categories
        WHERE id IN (:ids)
    """,
    )
    fun getCategoryEntities(ids: Set<Int>): Flow<List<CategoryEntity>>

    /**
     * Inserts [categoryEntities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCategories(categoryEntities: List<CategoryEntity>): List<Long>

    /**
     * Inserts or updates [entities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCategories(entities: List<CategoryEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(
        value = """
            DELETE FROM categories
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteCategories(ids: List<Int>)
}
