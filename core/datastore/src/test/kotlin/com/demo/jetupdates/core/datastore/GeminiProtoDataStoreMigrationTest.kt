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

import androidx.datastore.core.DataStoreFactory
import com.demo.jetupdates.core.datastore.ProtoChangeListVersionMigration
import com.demo.jetupdates.core.datastore.UserPreferencesSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

// Gemini version direct use of DataStoreFactory.create() not as DI Test replacement
// a bit cumbersome , see use of seedScope below
@OptIn(ExperimentalCoroutinesApi::class)
class GeminiProtoDataStoreMigrationTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun migrateLegacyIntsToStrings_preservesValues() = testScope.runTest {
        val testFile = tmpFolder.newFile("user_preferencesTest.pb")

        // 1. Create a dedicated scope for the seed DataStore
        val seedScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        // 1. Seed Proto DataStore with legacy integer values
        val initialDataStore = DataStoreFactory.create(
            serializer = UserPreferencesSerializer(),
            scope = seedScope,
            produceFile = { testFile },
        )
        initialDataStore.updateData { current ->
            current.toBuilder()
                .setDEPRECATEDCategoryChangeListVersion(42)
                .setDEPRECATEDShopItemChangeListVersion(105)
                .build()
        }

        // 2. Cancel the seed scope so DataStore releases the file lock
        seedScope.cancel()
        // 2. Instantiate DataStore with the migration attached
        val migratedDataStore = DataStoreFactory.create(
            serializer = UserPreferencesSerializer(),
            scope = testScope,
            migrations = listOf(ProtoChangeListVersionMigration),
            produceFile = { testFile },
        )

        // 3. Verify values are converted to strings and legacy fields are cleared
        val result = migratedDataStore.data.first()
        assertEquals("42", result.categoryChangeListVersion)
        assertEquals("105", result.shopItemChangeListVersion)
        assertEquals(0, result.deprecatedCategoryChangeListVersion)
        assertEquals(0, result.deprecatedShopItemChangeListVersion)
    }
}
