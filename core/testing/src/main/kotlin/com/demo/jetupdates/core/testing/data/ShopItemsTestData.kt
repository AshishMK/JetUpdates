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

@file:Suppress("ktlint:standard:max-line-length")

package com.demo.jetupdates.core.testing.data

import com.demo.jetupdates.core.model.data.ShopItem
import kotlinx.datetime.Instant

val shopItemsTestData: List<ShopItem> = listOf(
    ShopItem(
        id = 1,
        title = "Android Basics with Compose",
        price = 12f,
        description = "We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. You’ll learn the fundamentals of programming in Kotlin while building Android apps using Jetpack Compose, Android’s modern toolkit that simplifies and accelerates native UI development. These two units are just the beginning; more will be coming soon. Check out Android Basics with Compose to get started on your Android development journey",
        stock = 12,
        images = listOf("https://developer.android.com/images/hero-assets/android-basics-compose.svg"),
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Codelab",
        categories = listOf(categoriesTestData[1]),
    ),
    ShopItem(
        id = 2,
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        description = "Thank you everyone for following the Now in Android series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        price = 15f,
        images = listOf("https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg"),
        stock = 15,
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(categoriesTestData[0], categoriesTestData[1]),
    ),
    ShopItem(
        id = 3,
        title = "Transformations and customisations in the Paging Library",
        description = "A demonstration of different operations that can be performed " +
            "with Paging. Transformations like inserting separators, when to " +
            "create a new pager, and customisation options for consuming " +
            "PagingData.",
        price = 18f,
        images = listOf("https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg"),
        stock = 18,
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(categoriesTestData[2]),
    ),
    ShopItem(
        id = 4,
        title = "New Jetpack Release",
        description = "New Jetpack release includes updates to libraries such as CameraX, Benchmark, and" +
            "more!",
        price = 21f,
        images = emptyList(),
        stock = 21,
        publishDate = Instant.parse("2022-10-01T00:00:00.000Z"),
        type = "",
        categories = listOf(categoriesTestData[2]),
    ),
)
