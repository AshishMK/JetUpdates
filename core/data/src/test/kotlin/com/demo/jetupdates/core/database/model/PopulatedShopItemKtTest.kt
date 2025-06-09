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

import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.ShopItem
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class PopulatedShopItemKtTest {
    @Test
    fun populated_Shop_item_can_be_mapped_to_Shop_item() {
        val populatedShopItem = PopulatedShopItem(
            entity = ShopItemEntity(
                id = 1,
                title = "news",
                price = 10f,
                description = "Hilt",
                stock = 12,
                images = listOf("headerImageUrl"),
                type = "Video 📺",
                publishDate = Instant.fromEpochMilliseconds(1),
            ),
            categories = listOf(
                CategoryEntity(
                    id = 3,
                    name = "name",
                    shortDescription = "short description",
                    longDescription = "long description",
                    url = "URL",
                    imageUrl = "image URL",
                ),
            ),
        )
        val shopItem = populatedShopItem.asExternalModel()

        assertEquals(
            ShopItem(
                id = 1,
                title = "news",
                price = 10f,
                description = "Hilt",
                stock = 12,
                images = listOf("headerImageUrl"),
                type = "Video 📺",
                publishDate = Instant.fromEpochMilliseconds(1),
                categories = listOf(
                    Category(
                        id = 3,
                        name = "name",
                        shortDescription = "short description",
                        longDescription = "long description",
                        url = "URL",
                        imageUrl = "image URL",
                    ),
                ),
            ),
            shopItem,
        )
    }
}
