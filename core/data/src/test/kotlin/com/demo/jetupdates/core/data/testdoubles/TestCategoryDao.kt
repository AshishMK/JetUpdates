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

import com.demo.jetupdates.core.database.dao.CategoryDao
import com.demo.jetupdates.core.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Test double for [CategoryDao]
 */
class TestCategoryDao : CategoryDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<CategoryEntity>())

    override fun getCategoryEntity(categoryId: Int): Flow<CategoryEntity> =
        throw NotImplementedError("Unused in tests")

    override fun getCategoryEntities(): Flow<List<CategoryEntity>> = entitiesStateFlow

    override fun getCategoryEntities(ids: Set<Int>): Flow<List<CategoryEntity>> =
        getCategoryEntities().map { categories -> categories.filter { it.id in ids } }

    override suspend fun getOneOffCategoryEntities(): List<CategoryEntity> = emptyList()

    override suspend fun insertOrIgnoreCategories(categoryEntities: List<CategoryEntity>): List<Long> {
        // Keep old values over new values
        entitiesStateFlow.update { oldValues ->
            (oldValues + categoryEntities).distinctBy(CategoryEntity::id)
        }
        return categoryEntities.map { it.id.toLong() }
    }

    override suspend fun upsertCategories(entities: List<CategoryEntity>) {
        // Overwrite old values with new values
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(CategoryEntity::id) }
    }

    override suspend fun deleteCategories(ids: List<Int>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities -> entities.filterNot { it.id in idSet } }
    }
}
