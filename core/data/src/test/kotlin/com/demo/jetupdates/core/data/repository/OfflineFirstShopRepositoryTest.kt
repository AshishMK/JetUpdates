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
import com.demo.jetupdates.core.data.model.categoryCrossReferences
import com.demo.jetupdates.core.data.model.categoryEntityShells
import com.demo.jetupdates.core.data.testdoubles.CollectionType
import com.demo.jetupdates.core.data.testdoubles.CollectionType.ShopItems
import com.demo.jetupdates.core.data.testdoubles.TestAppNetworkDataSource
import com.demo.jetupdates.core.data.testdoubles.TestCategoryDao
import com.demo.jetupdates.core.data.testdoubles.TestShopItemDao
import com.demo.jetupdates.core.data.testdoubles.TestSynchronizer
import com.demo.jetupdates.core.data.testdoubles.filteredInterestsIds
import com.demo.jetupdates.core.data.testdoubles.nonPresentInterestsIds
import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.PopulatedShopItem
import com.demo.jetupdates.core.database.model.ShopItemCategoryCrossRef
import com.demo.jetupdates.core.database.model.ShopItemEntity
import com.demo.jetupdates.core.database.model.asExternalModel
import com.demo.jetupdates.core.datastore.AppPreferencesDataSource
import com.demo.jetupdates.core.datastore.UserPreferences
import com.demo.jetupdates.core.datastore.test.InMemoryDataStore
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem
import com.demo.jetupdates.core.testing.notifications.TestNotifier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineFirstShopRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstShopRepository

    private lateinit var appPreferencesDataSource: AppPreferencesDataSource

    private lateinit var shopItemDao: TestShopItemDao

    private lateinit var categoryDao: TestCategoryDao

    private lateinit var network: TestAppNetworkDataSource

    private lateinit var notifier: TestNotifier

    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        appPreferencesDataSource = AppPreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
        shopItemDao = TestShopItemDao()
        categoryDao = TestCategoryDao()
        network = TestAppNetworkDataSource()
        notifier = TestNotifier()
        synchronizer = TestSynchronizer(
            appPreferencesDataSource,
        )

        subject = OfflineFirstShopRepository(
            appPreferencesDataSource = appPreferencesDataSource,
            shopItemDao = shopItemDao,
            categoryDao = categoryDao,
            network = network,
            notifier = notifier,
        )
    }

    @Test
    fun offlineFirstShopRepository_shop_items_stream_is_backed_by_shop_item_dao() =
        testScope.runTest {
            subject.syncWith(synchronizer)
            assertEquals(
                shopItemDao.getShopItems()
                    .first()
                    .map(PopulatedShopItem::asExternalModel),
                subject.getShopItems()
                    .first(),
            )
        }

    @Test
    fun offlineFirstShopRepository_shop_items_for_category_is_backed_by_shop_item_dao() =
        testScope.runTest {
            assertEquals(
                expected = shopItemDao.getShopItems(
                    filterCategoryIds = filteredInterestsIds,
                    useFilterCategoryIds = true,
                )
                    .first()
                    .map(PopulatedShopItem::asExternalModel),
                actual = subject.getShopItems(
                    query = ShopItemQuery(
                        filterCategoryIds = filteredInterestsIds,
                    ),
                )
                    .first(),
            )

            assertEquals(
                expected = emptyList(),
                actual = subject.getShopItems(
                    query = ShopItemQuery(
                        filterCategoryIds = nonPresentInterestsIds,
                    ),
                )
                    .first(),
            )
        }

    @Test
    fun offlineFirstShopRepository_sync_pulls_from_network() =
        testScope.runTest {
            // User has not onboarded
            appPreferencesDataSource.setShouldHideOnboarding(false)
            subject.syncWith(synchronizer)

            val shopItemsFromNetwork = network.getShopItems()
                .map(NetworkShopItem::asEntity)
                .map(ShopItemEntity::asExternalModel)

            val shopItemsFromDb = shopItemDao.getShopItems()
                .first()
                .map(PopulatedShopItem::asExternalModel)

            assertEquals(
                shopItemsFromNetwork.map(ShopItem::id).sorted(),
                shopItemsFromDb.map(ShopItem::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(ShopItems),
                actual = synchronizer.getChangeListVersions().shopItemVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedShopItems.isEmpty())
        }

    @Test
    fun offlineFirstShopRepository_sync_deletes_items_marked_deleted_on_network() =
        testScope.runTest {
            // User has not onboarded
            appPreferencesDataSource.setShouldHideOnboarding(false)

            val shopItemsFromNetwork = network.getShopItems()
                .map(NetworkShopItem::asEntity)
                .map(ShopItemEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems = shopItemsFromNetwork
                .map(ShopItem::id)
                .partition { it.toString().chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.ShopItems,
                    id = it,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer)

            val shopItemsFromDb = shopItemDao.getShopItems()
                .first()
                .map(PopulatedShopItem::asExternalModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                expected = (shopItemsFromNetwork.map(ShopItem::id) - deletedItems).sorted(),
                actual = shopItemsFromDb.map(ShopItem::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.ShopItems),
                actual = synchronizer.getChangeListVersions().shopItemVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedShopItems.isEmpty())
        }

    @Test
    fun offlineFirstShopRepository_incremental_sync_pulls_from_network() =
        testScope.runTest {
            // User has not onboarded
            appPreferencesDataSource.setShouldHideOnboarding(false)

            // Set shop item version to 7
            synchronizer.updateChangeListVersions {
                copy(shopItemVersion = 7)
            }

            subject.syncWith(synchronizer)

            val changeList = network.changeListsAfter(
                CollectionType.ShopItems,
                version = 7,
            )
            val changeListIds = changeList
                .map(NetworkChangeList::id)
                .toSet()

            val shopItemsFromNetwork = network.getShopItems()
                .map(NetworkShopItem::asEntity)
                .map(ShopItemEntity::asExternalModel)
                .filter { it.id in changeListIds }

            val shopItemsFromDb = shopItemDao.getShopItems()
                .first()
                .map(PopulatedShopItem::asExternalModel)

            assertEquals(
                expected = shopItemsFromNetwork.map(ShopItem::id).sorted(),
                actual = shopItemsFromDb.map(ShopItem::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = changeList.last().changeListVersion,
                actual = synchronizer.getChangeListVersions().shopItemVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedShopItems.isEmpty())
        }

    @Test
    fun offlineFirstShopRepository_sync_saves_shell_category_entities() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getShopItems()
                    .map(NetworkShopItem::categoryEntityShells)
                    .flatten()
                    .distinctBy(CategoryEntity::id)
                    .sortedBy(CategoryEntity::toString),
                actual = categoryDao.getCategoryEntities()
                    .first()
                    .sortedBy(CategoryEntity::toString),
            )
        }

    @Test
    fun offlineFirstShopRepository_sync_saves_category_cross_references() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getShopItems()
                    .map(NetworkShopItem::categoryCrossReferences)
                    .flatten()
                    .distinct()
                    .sortedBy(ShopItemCategoryCrossRef::toString),
                actual = shopItemDao.categoryCrossReferences
                    .sortedBy(ShopItemCategoryCrossRef::toString),
            )
        }

    @Test
    fun offlineFirstShopRepository_sync_marks_as_read_on_first_run() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                network.getShopItems().map { it.id }.toSet(),
                appPreferencesDataSource.userData.first().viewedShopItems,
            )
        }

    @Test
    fun offlineFirstShopRepository_sync_does_not_mark_as_read_on_subsequent_run() =
        testScope.runTest {
            // Pretend that we already have up to change list 7
            synchronizer.updateChangeListVersions {
                copy(shopItemVersion = 7)
            }

            subject.syncWith(synchronizer)

            assertEquals(
                emptySet(),
                appPreferencesDataSource.userData.first().viewedShopItems,
            )
        }

    @Test
    fun offlineFirstShopRepository_sends_notifications_for_newly_synced_shop_item_that_is_followed() =
        testScope.runTest {
            // User has onboarded
            appPreferencesDataSource.setShouldHideOnboarding(true)

            val networkShopItems = network.getShopItems()

            // Follow roughly half the categories
            val followedCategoryIds = networkShopItems
                .flatMap(NetworkShopItem::categoryEntityShells)
                .mapNotNull { category ->
                    when (category.id.toString().chars().sum() % 2) {
                        0 -> category.id
                        else -> null
                    }
                }
                .toSet()

            // Set followed categories
            appPreferencesDataSource.setFollowedCategoryIds(followedCategoryIds)

            subject.syncWith(synchronizer)

            val followedShopItemIdsFromNetwork = networkShopItems
                .filter { (it.categories intersect followedCategoryIds).isNotEmpty() }
                .map(NetworkShopItem::id)
                .sorted()

            // Notifier should have been called with only shop items that have categories
            // that the user follows
            assertEquals(
                expected = followedShopItemIdsFromNetwork,
                actual = notifier.addedShopItems.first().map(ShopItem::id).sorted(),
            )
        }

    @Test
    fun offlineFirstShopRepository_does_not_send_notifications_for_existing_shop_items() =
        testScope.runTest {
            // User has onboarded
            appPreferencesDataSource.setShouldHideOnboarding(true)

            val networkShopItems = network.getShopItems()
                .map(NetworkShopItem::asEntity)

            val shopItems = networkShopItems
                .map(ShopItemEntity::asExternalModel)

            // Prepopulate dao with shop items
            shopItemDao.upsertShopItems(networkShopItems)

            val followedCategoryIds = shopItems
                .flatMap(ShopItem::categories)
                .map(Category::id)
                .toSet()

            // Follow all categories
            appPreferencesDataSource.setFollowedCategoryIds(followedCategoryIds)

            subject.syncWith(synchronizer)

            // Notifier should not have been called bc all shop items existed previously
            assertTrue(notifier.addedShopItems.isEmpty())
        }
}
