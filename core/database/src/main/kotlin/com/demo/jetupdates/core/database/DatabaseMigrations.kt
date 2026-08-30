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

package com.demo.jetupdates.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Automatic schema migrations sometimes require extra instructions to perform the migration, for
 * example, when a column is renamed. These extra instructions are placed here by creating a class
 * using the following naming convention `SchemaXtoY` where X is the schema version you're migrating
 * from and Y is the schema version you're migrating to. The class should implement
 * `AutoMigrationSpec`.
 */
internal object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Disable foreign keys temporarily during table recreation
            db.execSQL("PRAGMA foreign_keys=OFF;")

            // ==========================================
            // 1. MIGRATE TABLE: categories
            // ==========================================
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `categories_temp` (
                `id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `shortDescription` TEXT NOT NULL,
                `longDescription` TEXT NOT NULL DEFAULT '',
                `url` TEXT NOT NULL DEFAULT '',
                `imageUrl` TEXT NOT NULL DEFAULT '',
                PRIMARY KEY(`id`)
            )
                """.trimIndent(),
            )

            db.execSQL(
                """
            INSERT INTO `categories_temp` (`id`, `name`, `shortDescription`, `longDescription`, `url`, `imageUrl`)
            SELECT CAST(`id` AS TEXT), `name`, `shortDescription`, `longDescription`, `url`, `imageUrl`
            FROM `categories`
                """.trimIndent(),
            )

            db.execSQL("DROP TABLE `categories`")
            db.execSQL("ALTER TABLE `categories_temp` RENAME TO `categories`")

            // ==========================================
            // 2. MIGRATE TABLE: shop_items
            // ==========================================
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `shop_items_temp` (
                `id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `price` REAL NOT NULL,
                `description` TEXT NOT NULL,
                `stock` INTEGER NOT NULL,
                `images` TEXT NOT NULL,
                `publish_date` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
                """.trimIndent(),
            )

            db.execSQL(
                """
            INSERT INTO `shop_items_temp` (`id`, `name`, `price`, `description`, `stock`, `images`, `publish_date`, `type`)
            SELECT CAST(`id` AS TEXT), `name`, `price`, `description`, `stock`, `images`, `publish_date`, `type`
            FROM `shop_items`
                """.trimIndent(),
            )

            db.execSQL("DROP TABLE `shop_items`")
            db.execSQL("ALTER TABLE `shop_items_temp` RENAME TO `shop_items`")

            // ==========================================
            // 3. MIGRATE CROSS-REF TABLE: shop_items_categories
            // ==========================================
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `shop_items_categories_temp` (
                `shop_item_id` TEXT NOT NULL,
                `category_id` TEXT NOT NULL,
                PRIMARY KEY(`shop_item_id`, `category_id`),
                FOREIGN KEY(`shop_item_id`) REFERENCES `shop_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
                """.trimIndent(),
            )

            db.execSQL(
                """
            INSERT INTO `shop_items_categories_temp` (`shop_item_id`, `category_id`)
            SELECT CAST(`shop_item_id` AS TEXT), CAST(`category_id` AS TEXT)
            FROM `shop_items_categories`
                """.trimIndent(),
            )

            db.execSQL("DROP TABLE `shop_items_categories`")
            db.execSQL("ALTER TABLE `shop_items_categories_temp` RENAME TO `shop_items_categories`")

            // Re-create indices for the junction table
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_shop_items_categories_shop_item_id` ON `shop_items_categories` (`shop_item_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_shop_items_categories_category_id` ON `shop_items_categories` (`category_id`)")

            // ==========================================
            // 4. MIGRATE FTS TABLES
            // ==========================================

            // FTS: shopItemsFts
            db.execSQL("DROP TABLE IF EXISTS `shopItemsFts`")
            db.execSQL(
                """
            CREATE VIRTUAL TABLE IF NOT EXISTS `shopItemsFts` USING fts4(
                `shopItemId` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `description` TEXT NOT NULL
            )
                """.trimIndent(),
            )
            db.execSQL(
                """
            INSERT INTO `shopItemsFts` (`shopItemId`, `name`, `description`)
            SELECT `id`, `name`, `description` FROM `shop_items`
                """.trimIndent(),
            )

            // FTS: categoriesFts
            db.execSQL("DROP TABLE IF EXISTS `categoriesFts`")
            db.execSQL(
                """
            CREATE VIRTUAL TABLE IF NOT EXISTS `categoriesFts` USING fts4(
                `categoryId` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `shortDescription` TEXT NOT NULL, 
                `longDescription` TEXT NOT NULL
            )
                """.trimIndent(),
            )
            db.execSQL(
                """
            INSERT INTO `categoriesFts` (`categoryId`, `name`, `shortDescription`, `longDescription`)
            SELECT `id`, `name`, `shortDescription`, `longDescription` FROM `categories`
                """.trimIndent(),
            )

            // Re-enable foreign keys
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }
}
