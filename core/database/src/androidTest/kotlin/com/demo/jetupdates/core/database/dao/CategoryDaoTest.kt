/*
 * Copyright 2024 The Android Open Source Project
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

internal class CategoryDaoTest : DatabaseTest() {

    @Test
    fun getCategories() = runTest {
        insertCategories()

        val savedCategories = categoryDao.getCategoryEntities().first()

        assertEquals(
            listOf(1, 2, 3),
            savedCategories.map { it.id },
        )
    }

    @Test
    fun getCategory() = runTest {
        insertCategories()

        val savedCategoryEntity = categoryDao.getCategoryEntity(2).first()

        assertEquals("performance", savedCategoryEntity.name)
    }

    @Test
    fun getCategories_oneOff() = runTest {
        insertCategories()

        val savedCategories = categoryDao.getOneOffCategoryEntities()

        assertEquals(
            listOf(1, 2, 3),
            savedCategories.map { it.id },
        )
    }

    @Test
    fun getCategories_byId() = runTest {
        insertCategories()

        val savedCategories = categoryDao.getCategoryEntities(setOf(1, 2))
            .first()

        assertEquals(listOf("compose", "performance"), savedCategories.map { it.name })
    }

    @Test
    fun insertCategory_newEntryIsIgnoredIfAlreadyExists() = runTest {
        insertCategories()
        categoryDao.insertOrIgnoreCategories(
            listOf(testCategoryEntity(1, "compose")),
        )

        val savedCategories = categoryDao.getOneOffCategoryEntities()

        assertEquals(3, savedCategories.size)
    }

    @Test
    fun upsertCategory_existingEntryIsUpdated() = runTest {
        insertCategories()
        categoryDao.upsertCategories(
            listOf(testCategoryEntity(1, "newName")),
        )

        val savedCategories = categoryDao.getOneOffCategoryEntities()

        assertEquals(3, savedCategories.size)
        assertEquals("newName", savedCategories.first().name)
    }

    @Test
    fun deleteCategories_byId_existingEntriesAreDeleted() = runTest {
        insertCategories()
        categoryDao.deleteCategories(listOf(1, 2))

        val savedCategories = categoryDao.getOneOffCategoryEntities()

        assertEquals(1, savedCategories.size)
        assertEquals(3, savedCategories.first().id)
    }

    private suspend fun insertCategories() {
        val categoryEntities = listOf(
            testCategoryEntity(1, "compose"),
            testCategoryEntity(2, "performance"),
            testCategoryEntity(3, "headline"),
        )
        categoryDao.insertOrIgnoreCategories(categoryEntities)
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
