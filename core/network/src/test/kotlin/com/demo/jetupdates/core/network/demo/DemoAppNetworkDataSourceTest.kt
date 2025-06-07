/*
 * Copyright 2024 The Android Open Source Project
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

package com.demo.jetupdates.core.network.demo

import JvmUnitTestDemoAssetManager
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkShopItem
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class
DemoAppNetworkDataSourceTest {

    private lateinit var subject: DemoAppNetworkDataSource

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        subject = DemoAppNetworkDataSource(
            ioDispatcher = testDispatcher,
            networkJson = Json { ignoreUnknownKeys = true },
            assets = JvmUnitTestDemoAssetManager,
        )
    }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun testDeserializationOfCategories() = runTest(testDispatcher) {
        assertEquals(
            NetworkCategory(
                id = 1,
                name = "Fashion & Apparel",
                shortDescription = "Stay Stylish, Every Day",
                longDescription = "Discover the latest trends in clothing, footwear, and accessories for men, women, and kids. Shop timeless classics and fresh designs for every season.",
                url = "",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            ),
            subject.getCategories().first(),
        )
    }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun testDeserializationOfShopItems() = runTest(testDispatcher) {
        assertEquals(
            NetworkShopItem(
                id = 125,
                title = "Advanced Children's Puzzle",
                price = 218.42f,
                description = "The advanced children's puzzle is engineered using cutting-edge materials to ensure lasting durability. Featuring a thoughtfully designed structure, this product is ideal for those seeking quality and reliability. With attention to small details and a commitment to user satisfaction, it is sure to become a favorite in your daily essentials.",
                stock = 199,
                images = listOf("https://od.lk/d/NzlfNTQxMjE3NTJf/prod_19.jpg", "https://od.lk/d/NzlfNTQxMjE3NTNf/prod_20.jpg"),
                publishDate = LocalDateTime(
                    year = 2021,
                    monthNumber = 10,
                    dayOfMonth = 9,
                    hour = 23,
                    minute = 0,
                    second = 0,
                    nanosecond = 0,
                ).toInstant(TimeZone.UTC),
                type = "2",
                categories = listOf(16, 14),
            ),
            subject.getShopItems().find { it.id == 125 },
        )
    }
}
