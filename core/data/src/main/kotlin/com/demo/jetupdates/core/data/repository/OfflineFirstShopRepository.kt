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
import com.demo.jetupdates.core.data.changeListSync
import com.demo.jetupdates.core.data.model.asEntity
import com.demo.jetupdates.core.data.model.categoryCrossReferences
import com.demo.jetupdates.core.data.model.categoryEntityShells
import com.demo.jetupdates.core.database.dao.CategoryDao
import com.demo.jetupdates.core.database.dao.ShopItemDao
import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.PopulatedShopItem
import com.demo.jetupdates.core.database.model.asExternalModel
import com.demo.jetupdates.core.datastore.AppPreferencesDataSource
import com.demo.jetupdates.core.datastore.ChangeListVersions
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.network.AppNetworkDataSource
import com.demo.jetupdates.core.network.model.NetworkShopItem
import com.demo.jetupdates.core.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Heuristic value to optimize for serialization and deserialization cost on client and server
// for each shop item batch.
private const val SYNC_BATCH_SIZE = 40

/**
 * Disk storage backed implementation of the [ShopRepository].
 * Reads are exclusively from local storage to support offline access.
 */
internal class OfflineFirstShopRepository @Inject constructor(
    private val appPreferencesDataSource: AppPreferencesDataSource,
    private val shopItemDao: ShopItemDao,
    private val categoryDao: CategoryDao,
    private val network: AppNetworkDataSource,
    private val notifier: Notifier,
) : ShopRepository {

    override fun getShopItems(query: ShopItemQuery): Flow<List<ShopItem>> =
        shopItemDao.getShopItems(
            useFilterCategoryIds = query.filterCategoryIds != null,
            filterCategoryIds = query.filterCategoryIds ?: emptySet(),
            useFilterItemIds = query.filterItemIds != null,
            filterItemIds = query.filterItemIds ?: emptySet(),
        )
            .map { it.map(PopulatedShopItem::asExternalModel) }

    override fun getShopItem(id: Int): Flow<ShopItem> =
        shopItemDao.getShopItem(id).map(PopulatedShopItem::asExternalModel)

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::shopItemVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                network.getShopItemChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(shopItemVersion = latestVersion)
            },
            modelDeleter = shopItemDao::deleteShopItems,
            modelUpdater = {
                    changedIds ->
                val userData = appPreferencesDataSource.userData.first()
                val hasOnboarded = userData.shouldHideOnboarding
                val followedCategoryIds = userData.followedCategories

                val existingShopItemIdsThatHaveChanged = when {
                    hasOnboarded -> shopItemDao.getShopItemIds(
                        useFilterCategoryIds = true,
                        filterCategoryIds = followedCategoryIds,
                        useFilterItemIds = true,
                        filterItemIds = changedIds.toSet(),
                    )
                        .first()
                        .toSet()
                    // No need to retrieve anything if notifications won't be sent
                    else -> emptySet()
                }

                if (isFirstSync) {
                    // When we first retrieve shop item, mark everything viewed, so that we aren't
                    // overwhelmed with all historical shop item.
                    appPreferencesDataSource.setShopItemsViewed(changedIds, true)
                }

                // Obtain the shop items which have changed from the network and upsert them locally
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkShopItems = network.getShopItems(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!

                    categoryDao.insertOrIgnoreCategories(
                        categoryEntities = networkShopItems
                            .map(NetworkShopItem::categoryEntityShells)
                            .flatten()
                            .distinctBy(CategoryEntity::id),
                    )
                    shopItemDao.upsertShopItems(
                        shopItemEntities = networkShopItems.map(
                            NetworkShopItem::asEntity,
                        ),
                    )
                    shopItemDao.insertOrIgnoreCategoryCrossRefEntities(
                        shopItemCategoryCrossReferences = networkShopItems
                            .map(NetworkShopItem::categoryCrossReferences)
                            .distinct()
                            .flatten(),
                    )
                }

                if (hasOnboarded) {
                    val addedShopItems = shopItemDao.getShopItems(
                        useFilterCategoryIds = true,
                        filterCategoryIds = followedCategoryIds,
                        useFilterItemIds = true,
                        filterItemIds = changedIds.toSet() - existingShopItemIdsThatHaveChanged,
                    )
                        .first()
                        .map(PopulatedShopItem::asExternalModel)

                    if (addedShopItems.isNotEmpty()) {
                        notifier.postShopNotifications(
                            shopItems = addedShopItems,
                        )
                    }
                }
            },
        )
    }
}
