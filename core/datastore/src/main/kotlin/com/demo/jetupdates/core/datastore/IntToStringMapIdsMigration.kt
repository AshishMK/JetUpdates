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

package com.demo.jetupdates.core.datastore

import androidx.datastore.core.DataMigration

/**
 * Migrates saved ids from [Int] to [String] types
 */
internal object IntToStringMapIdsMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate topic ids
            followedCategoryIds.clear()
            followedCategoryIds.putAll(
                currentData.deprecatedFollowedCategoryIdsMap.mapKeys { it.key.toString() },
            )
            dEPRECATEDFollowedCategoryIds.clear()

            // Migrate author ids
            followedAuthorIds.clear()
            followedAuthorIds.putAll(
                currentData.deprecatedFollowedAuthorIdsMap.mapKeys { it.key.toString() },
            )
            dEPRECATEDFollowedAuthorIds.clear()

            // Migrate author ids
            bookmarkedShopItemIds.clear()
            bookmarkedShopItemIds.putAll(
                currentData.deprecatedBookmarkedShopItemIdsMap.mapKeys { it.key.toString() },
            )
            dEPRECATEDBookmarkedShopItemIds.clear()

            viewedShopItemIds.clear()
            viewedShopItemIds.putAll(
                currentData.deprecatedViewedShopItemIdsMap.mapKeys { it.key.toString() },
            )
            dEPRECATEDViewedShopItemIds.clear()

            // Mark migration as complete
            hasDoneIntToStringIdMigration = true
        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneIntToStringIdMigration
}
