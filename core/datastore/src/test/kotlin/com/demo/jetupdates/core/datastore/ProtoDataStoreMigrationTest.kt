package com.demo.jetupdates.core.datastore

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class ProtoDataStoreMigrationTest {

    @Test
    fun migrateLegacyIntsToStrings_preservesValues() = runTest {
        // Set up existing preferences with topic int ids
        val preMigrationUserPreferences = userPreferences {
            dEPRECATEDCategoryChangeListVersion = 2
            dEPRECATEDShopItemChangeListVersion = 4
        }

        // Assert that there are no string Version ids yet
        assertEquals(
            "",
            preMigrationUserPreferences.categoryChangeListVersion,
        )
        assertEquals(
            "",
            preMigrationUserPreferences.shopItemChangeListVersion,
        )

        // Run the migration
        val postMigrationUserPreferences =
            ProtoChangeListVersionMigration.migrate(preMigrationUserPreferences)

        assertEquals(
            userPreferences {
                categoryChangeListVersion = "2"
                shopItemChangeListVersion = "4"
            },
            postMigrationUserPreferences,
        )

        // Assert that the migration has been marked complete
        //Note after the migration deprecatedCategoryChangeListVersion and deprecatedShopItemChangeListVersion are reset to 0
        // As we Call  clearDEPRECATEDCategoryChangeListVersion() and clearDEPRECATEDShopItemChangeListVersion
        assertEquals(0, preMigrationUserPreferences.deprecatedCategoryChangeListVersion)
        assertEquals(0, preMigrationUserPreferences.deprecatedShopItemChangeListVersion)

    }
}