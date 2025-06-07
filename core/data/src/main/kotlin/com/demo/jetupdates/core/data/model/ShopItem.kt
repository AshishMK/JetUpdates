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

package com.demo.jetupdates.core.data.model

import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.ShopItemCategoryCrossRef
import com.demo.jetupdates.core.database.model.ShopItemEntity
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkShopItem
import com.demo.jetupdates.core.network.model.asExternalModel

fun NetworkShopItem.asEntity() = ShopItemEntity(
    id = id,
    title = title,
    price = price,
    description = description,
    stock = stock,
    images = images,
    publishDate = publishDate,
    type = type,
)

/**
 * A shell [CategoryEntity] to fulfill the foreign key constraint when inserting
 * a [ShopItemEntity] into the DB
 */
fun NetworkShopItem.categoryEntityShells() =
    categories.map { categoryId ->
        CategoryEntity(
            id = categoryId,
            name = "",
            url = "",
            imageUrl = "",
            shortDescription = "",
            longDescription = "",
        )
    }

fun NetworkShopItem.categoryCrossReferences(): List<ShopItemCategoryCrossRef> =
    categories.map { categoryId ->
        ShopItemCategoryCrossRef(
            shopItemId = id,
            categoryId = categoryId,
        )
    }

fun NetworkShopItem.asExternalModel(categories: List<NetworkCategory>) =
    ShopItem(
        id = id,
        title = title,
        price = price,
        description = description,
        stock = stock,
        images = images,
        publishDate = publishDate,
        type = type,
        categories = categories
            .filter { networkCategory -> this.categories.contains(networkCategory.id) }
            .map(NetworkCategory::asExternalModel),
    )
