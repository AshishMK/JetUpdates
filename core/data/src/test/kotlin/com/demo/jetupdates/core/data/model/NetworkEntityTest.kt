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

import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkShopItem
import com.demo.jetupdates.core.network.model.asExternalModel
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class NetworkEntityTest {

    @Test
    fun networkCategoryMapsToDatabaseModel() {
        val networkModel = NetworkCategory(
            id = 0,
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "image URL",
        )
        val entity = networkModel.asEntity()

        assertEquals(0, entity.id)
        assertEquals("Test", entity.name)
        assertEquals("short description", entity.shortDescription)
        assertEquals("long description", entity.longDescription)
        assertEquals("URL", entity.url)
        assertEquals("image URL", entity.imageUrl)
    }

    @Test
    fun networkShopItemMapsToDatabaseModel() {
        val networkModel =
            NetworkShopItem(
                id = 0,
                title = "title",
                price = 10.02f,
                description = "description",
                stock = 178,
                images = listOf("headerImageUrl", "headerImageUrl2"),
                publishDate = Instant.fromEpochMilliseconds(1),
                type = "Article 📚",
            )
        val entity = networkModel.asEntity()

        assertEquals(0, entity.id)
        assertEquals("title", entity.title)
        assertEquals(10.02f, entity.price)
        assertEquals("description", entity.description)
        assertEquals(178, entity.stock)
        assertEquals(listOf("headerImageUrl", "headerImageUrl2"), entity.images)
        assertEquals(Instant.fromEpochMilliseconds(1), entity.publishDate)
        assertEquals("Article 📚", entity.type)
    }

    @Test
    fun networkCategoryMapsToExternalModel() {
        val networkCategory = NetworkCategory(
            id = 0,
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "imageUrl",
        )

        val expected = Category(
            id = 0,
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "imageUrl",
        )

        assertEquals(expected, networkCategory.asExternalModel())
    }

    @Test
    fun networkShopItemMapsToExternalModel() {
        val networkShopItem = NetworkShopItem(
            id = 0,
            title = "title",
            price = 12f,
            description = "description",
            stock = 100,
            images = listOf("headerImageUrl", "image2"),
            publishDate = Instant.fromEpochMilliseconds(1),
            type = "Article 📚",
            categories = listOf(1, 2),
        )

        val networkCategories = listOf(
            NetworkCategory(
                id = 1,
                name = "Test 1",
                shortDescription = "short description 1",
                longDescription = "long description 1",
                url = "url 1",
                imageUrl = "imageUrl 1",
            ),
            NetworkCategory(
                id = 2,
                name = "Test 2",
                shortDescription = "short description 2",
                longDescription = "long description 2",
                url = "url 2",
                imageUrl = "imageUrl 2",
            ),
        )

        val expected = ShopItem(
            id = 0,
            title = "title",
            price = 12f,
            description = "description",
            stock = 100,
            images = listOf("headerImageUrl", "image2"),
            publishDate = Instant.fromEpochMilliseconds(1),
            type = "Article 📚",
            categories = networkCategories.map(NetworkCategory::asExternalModel),
        )
        assertEquals(expected, networkShopItem.asExternalModel(networkCategories))
    }
}
