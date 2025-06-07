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

package com.demo.jetupdates.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross reference for many to many relationship between [ShopCategoryEntity] and [CategoryEntity]
 */
@Entity(
    tableName = "shop_items_categories",
    primaryKeys = ["shop_item_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = ShopItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_item_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["shop_item_id"]),
        Index(value = ["category_id"]),
    ],
)
data class ShopItemCategoryCrossRef(
    @ColumnInfo(name = "shop_item_id")
    val shopItemId: Int,
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
)
