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

package com.demo.jetupdates.core.network.apollo

import com.demo.jetupdates.core.network.GetCategoriesByIdsQuery
import com.demo.jetupdates.core.network.GetCategoryChangeListQuery
import com.demo.jetupdates.core.network.GetShopItemChangeListQuery
import com.demo.jetupdates.core.network.GetShopItemsByIdsQuery
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem
import kotlinx.datetime.Instant

object GraphQLExtensions {

    fun GetCategoryChangeListQuery.Category.asNetworkChangeList() = NetworkChangeList(
        id = id,
        changeListVersion = updatedAt,
        isDelete = isDeleted,

    )

    fun GetShopItemChangeListQuery.ShopItem.asNetworkChangeList() = NetworkChangeList(
        id = id,
        changeListVersion = updatedAt,
        isDelete = isDeleted,
    )

    fun GetCategoriesByIdsQuery.Category.asNetworkCategory() = NetworkCategory(
        id = id,
        name = name,
        imageUrl = imageUrl ?: "",
        shortDescription = shortDescription ?: "",
        longDescription = longDescription ?: "",
        url = url ?: "",
        updatedAt = updatedAt,
    )

    fun GetShopItemsByIdsQuery.ShopItem.asNetworkShopItem() = NetworkShopItem(
        id = id,
        title = title,
        price = price.toFloat(),
        description = description ?: "",
        stock = stock,
        images = images,
        publishDate = Instant.parse(publishDate),
        type = type,
        categories = categories,

    )
}
