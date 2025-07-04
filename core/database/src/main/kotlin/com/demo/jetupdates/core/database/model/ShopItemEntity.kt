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
import androidx.room.PrimaryKey
import com.demo.jetupdates.core.model.data.ShopItem
import kotlinx.datetime.Instant

/**
 * Defines an JU App Shop Item.
 */
@Entity(
    tableName = "shop_items",
)
data class ShopItemEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "name")
    val title: String,
    val price: Float,
    val description: String,
    val stock: Int,
    val images: List<String>,
    @ColumnInfo(name = "publish_date")
    val publishDate: Instant,
    val type: String,
)

fun ShopItemEntity.asExternalModel() = ShopItem(
    id = id,
    title = title,
    price = price,
    description = description,
    stock = stock,
    images = images,
    publishDate = publishDate,
    type = type,
    categories = listOf(),
)
