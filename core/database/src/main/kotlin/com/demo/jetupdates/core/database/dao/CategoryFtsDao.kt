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

package com.demo.jetupdates.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.jetupdates.core.database.model.CategoryFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [CategoryFtsEntity] access.
 */
@Dao
interface CategoryFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryFtsEntity>)

    @Query("SELECT categoryId FROM categoriesFts WHERE categoriesFts MATCH :query")
    fun searchAllCategories(query: String): Flow<List<Int>>

    @Query("SELECT count(*) FROM categoriesFts")
    fun getCount(): Flow<Int>
}
