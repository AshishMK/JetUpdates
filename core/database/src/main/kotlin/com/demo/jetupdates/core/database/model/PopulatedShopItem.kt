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

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.demo.jetupdates.core.model.data.ShopItem

/**
 * External data layer representation of a fully populated JU App shop item
 */
data class PopulatedShopItem(
    @Embedded
    val entity: ShopItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ShopItemCategoryCrossRef::class,
            parentColumn = "shop_item_id",
            entityColumn = "category_id",
        ),
    )
    val categories: List<CategoryEntity>,
)

fun PopulatedShopItem.asExternalModel() = ShopItem(
    id = entity.id,
    title = entity.title,
    price = entity.price,
    description = entity.description,
    stock = entity.stock,
    images = entity.images,
    publishDate = entity.publishDate,
    type = entity.type,
    categories = categories.map(CategoryEntity::asExternalModel),
)

fun PopulatedShopItem.asFtsEntity() = ShopItemFtsEntity(
    shopItemId = entity.id,
    title = entity.title,
    description = entity.description,
)
