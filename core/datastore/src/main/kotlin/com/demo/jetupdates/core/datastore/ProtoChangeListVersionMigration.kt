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