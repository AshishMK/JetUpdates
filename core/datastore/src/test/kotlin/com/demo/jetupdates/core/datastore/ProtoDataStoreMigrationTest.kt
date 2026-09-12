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
        // Note after the migration deprecatedCategoryChangeListVersion and deprecatedShopItemChangeListVersion are reset to 0
        // As we Call  clearDEPRECATEDCategoryChangeListVersion() and clearDEPRECATEDShopItemChangeListVersio
        // we can not clear the postMigrationUserPreferences
        assertEquals(0, postMigrationUserPreferences.deprecatedCategoryChangeListVersion)
        assertEquals(0, postMigrationUserPreferences.deprecatedShopItemChangeListVersion)
    }
}
