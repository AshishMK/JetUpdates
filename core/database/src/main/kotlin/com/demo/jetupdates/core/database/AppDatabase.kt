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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.jetupdates.core.database.dao.CategoryDao
import com.demo.jetupdates.core.database.dao.CategoryFtsDao
import com.demo.jetupdates.core.database.dao.RecentSearchQueryDao
import com.demo.jetupdates.core.database.dao.ShopItemDao
import com.demo.jetupdates.core.database.dao.ShopItemFtsDao
import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.CategoryFtsEntity
import com.demo.jetupdates.core.database.model.RecentSearchQueryEntity
import com.demo.jetupdates.core.database.model.ShopItemCategoryCrossRef
import com.demo.jetupdates.core.database.model.ShopItemEntity
import com.demo.jetupdates.core.database.model.ShopItemFtsEntity
import com.demo.jetupdates.core.database.util.InstantConverter

@Database(
    entities = [
        ShopItemEntity::class,
        ShopItemCategoryCrossRef::class,
        ShopItemFtsEntity::class,
        CategoryEntity::class,
        CategoryFtsEntity::class,
        RecentSearchQueryEntity::class,
    ],
    version = 1,

    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun shopItemDao(): ShopItemDao
    abstract fun categoryFtsDao(): CategoryFtsDao
    abstract fun shopItemFtsDao(): ShopItemFtsDao
    abstract fun recentSearchQueryDao(): RecentSearchQueryDao
}
