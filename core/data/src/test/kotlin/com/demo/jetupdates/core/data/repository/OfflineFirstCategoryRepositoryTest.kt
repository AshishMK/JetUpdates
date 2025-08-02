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

package com.demo.jetupdates.core.data.repository

import com.demo.jetupdates.core.data.Synchronizer
import com.demo.jetupdates.core.data.model.asEntity
import com.demo.jetupdates.core.data.testdoubles.CollectionType
import com.demo.jetupdates.core.data.testdoubles.TestAppNetworkDataSource
import com.demo.jetupdates.core.data.testdoubles.TestCategoryDao
import com.demo.jetupdates.core.database.dao.CategoryDao
import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.asExternalModel
import com.demo.jetupdates.core.datastore.AppPreferencesDataSource
import com.demo.jetupdates.core.datastore.UserPreferences
import com.demo.jetupdates.core.datastore.test.InMemoryDataStore
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.network.model.NetworkCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class OfflineFirstCategoryRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstCategoriesRepository

    private lateinit var categoryDao: CategoryDao

    private lateinit var network: TestAppNetworkDataSource

    private lateinit var appPreferences: AppPreferencesDataSource

    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        categoryDao = TestCategoryDao()
        network = TestAppNetworkDataSource()
        appPreferences =
            AppPreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
        synchronizer = TestSynchronizer(appPreferences)

        subject = OfflineFirstCategoriesRepository(
            categoryDao = categoryDao,
            network = network,
        )
    }

    @Test
    fun offlineFirstCategoriesRepository_categories_stream_is_backed_by_categories_dao() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                categoryDao.getCategoryEntities()
                    .first()
                    .map(CategoryEntity::asExternalModel),
                subject.getCategories()
                    .first(),
            )
        }

    @Test
    fun offlineFirstCategoriesRepository_sync_pulls_from_network() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            val networkCategories = network.getCategories()
                .map(NetworkCategory::asEntity)

            val dbCategories = categoryDao.getCategoryEntities()
                .first()

            assertEquals(
                networkCategories.map(CategoryEntity::id),
                dbCategories.map(CategoryEntity::id),
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion(CollectionType.Categories),
                synchronizer.getChangeListVersions().categoryVersion,
            )
        }

    @Test
    fun offlineFirstCategoriesRepository_incremental_sync_pulls_from_network() =
        testScope.runTest {
            // Set categories version to 10
            synchronizer.updateChangeListVersions {
                copy(categoryVersion = 10)
            }

            subject.syncWith(synchronizer)

            val networkCategories = network.getCategories()
                .map(NetworkCategory::asEntity)
                // Drop 10 to simulate the first 10 items being unchanged
                .drop(10)

            val dbCategories = categoryDao.getCategoryEntities()
                .first()

            assertEquals(
                networkCategories.map(CategoryEntity::id),
                dbCategories.map(CategoryEntity::id),
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion(CollectionType.Categories),
                synchronizer.getChangeListVersions().categoryVersion,
            )
        }

    @Test
    fun offlineFirstCategoriesRepository_sync_deletes_items_marked_deleted_on_network() =
        testScope.runTest {
            val networkCategories = network.getCategories()
                .map(NetworkCategory::asEntity)
                .map(CategoryEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems = networkCategories
                .map(Category::id)
                .partition { it.toString().chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.Categories,
                    id = it,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer)

            val dbCategories = categoryDao.getCategoryEntities()
                .first()
                .map(CategoryEntity::asExternalModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                networkCategories.map(Category::id) - deletedItems,
                dbCategories.map(Category::id),
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion(CollectionType.Categories),
                synchronizer.getChangeListVersions().categoryVersion,
            )
        }
}
