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

package com.demo.jetupdates.core.database.dao

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.demo.jetupdates.core.database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class DatabaseMigrationTest {

    private val testDb = "migration-test.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        // 1. Create DB at version 2 using the exported schema JSON asset
        var db = helper.createDatabase(testDb, 2).apply {
            execSQL("INSERT INTO categories (id, name, shortDescription) VALUES ('cat_1', 'Electronics', 'url')")
            close()
        }

        // 2. Run AutoMigration to version 3 (Adds the 'updatedAt' column)
        db = helper.runMigrationsAndValidate(testDb, 3, true)

        // 3. Verify old data survived and 'updatedAt' is null for existing rows
        val cursor = db.query("SELECT * FROM categories WHERE id = 'cat_1'")
        assertTrue(cursor.moveToFirst())

        val updatedAtColumnIndex = cursor.getColumnIndex("updatedAt")
        assertNotEquals(-1, updatedAtColumnIndex)
        assertTrue(cursor.isNull(updatedAtColumnIndex))
        cursor.close()

        // 4. Now that the DB is at version 3, you can insert rows containing 'updatedAt'
        db.execSQL("INSERT INTO categories (id, name, shortDescription, updatedAt) VALUES ('cat_2', 'Electronics', 'url', 'kaal')")

        val cursor2 = db.query("SELECT * FROM categories WHERE id = 'cat_2'")
        assertTrue(cursor2.moveToFirst())
        assertFalse(cursor2.isNull(updatedAtColumnIndex))
        assertEquals("kaal", cursor2.getString(updatedAtColumnIndex))
        cursor2.close()
    }
}
