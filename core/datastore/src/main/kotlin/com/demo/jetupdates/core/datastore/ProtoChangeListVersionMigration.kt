/*
 * Copyright 2026 The Android Open Source Project
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

object ProtoChangeListVersionMigration : DataMigration<UserPreferences> {

    override suspend fun migrate(currentData: UserPreferences): UserPreferences {
        return currentData.toBuilder().apply {
            // Migrate Category version
            if (currentData.deprecatedCategoryChangeListVersion != 0) {
                setCategoryChangeListVersion(currentData.deprecatedCategoryChangeListVersion.toString())
                clearDEPRECATEDCategoryChangeListVersion()
            }

            // Migrate Author version
            if (currentData.deprecatedAuthorChangeListVersion != 0) {
                setAuthorChangeListVersion(currentData.deprecatedAuthorChangeListVersion.toString())
                clearDEPRECATEDAuthorChangeListVersion()
            }

            // Migrate ShopItem version
            if (currentData.deprecatedShopItemChangeListVersion != 0) {
                setShopItemChangeListVersion(currentData.deprecatedShopItemChangeListVersion.toString())
                clearDEPRECATEDShopItemChangeListVersion()
            }
        }.build()
    }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean {
        // Check if any legacy int field holds a value that hasn't been migrated yet
        return currentData.deprecatedCategoryChangeListVersion != 0 ||
            currentData.deprecatedAuthorChangeListVersion != 0 ||
            currentData.deprecatedShopItemChangeListVersion != 0
    }

    override suspend fun cleanUp() {
        // No additional cleanup needed
    }
}
