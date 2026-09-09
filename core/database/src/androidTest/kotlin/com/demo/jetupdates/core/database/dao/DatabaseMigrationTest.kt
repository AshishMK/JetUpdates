package com.demo.jetupdates.core.database.dao

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.demo.jetupdates.core.database.AppDatabase
import org.junit.Rule
import androidx.room.testing.MigrationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.*

class DatabaseMigrationTest {

    private val TEST_DB = "migration-test.db"
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        // 1. Create DB at version 2 using the exported schema JSON asset
        var db = helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO categories (id, name, shortDescription) VALUES ('cat_1', 'Electronics', 'url')")
            close()
        }


        // 2. Run AutoMigration to version 3 (Adds the 'updatedAt' column)
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true)

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