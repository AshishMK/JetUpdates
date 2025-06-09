/*
 * Copyright 2023 The Android Open Source Project
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

package com.demo.jetupdates.core.data

import com.demo.jetupdates.core.model.data.DarkThemeConfig
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.model.data.ThemeBrand
import com.demo.jetupdates.core.model.data.UserData
import com.demo.jetupdates.core.model.data.UserShopItem
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

val userShopItemsTestData: List<UserShopItem> = UserData(
    bookmarkedShopItems = setOf(1, 4),
    viewedShopItems = setOf(1, 2, 4),
    followedCategories = emptySet(),
    themeBrand = ThemeBrand.ANDROID,
    darkThemeConfig = DarkThemeConfig.DARK,
    shouldHideOnboarding = true,
    useDynamicColor = false,
).let { userData ->
    listOf(
        UserShopItem(
            shopItem = ShopItem(
                id = 1,
                title = "Android Basics with Compose",
                description = "We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. You’ll learn the fundamentals of programming in Kotlin while building Android apps using Jetpack Compose, Android’s modern toolkit that simplifies and accelerates native UI development. These two units are just the beginning; more will be coming soon. Check out Android Basics with Compose to get started on your Android development journey",
                price = 12f,
                images = listOf("https://developer.android.com/images/hero-assets/android-basics-compose.svg"),
                stock = 12,
                publishDate = LocalDateTime(
                    year = 2022,
                    monthNumber = 5,
                    dayOfMonth = 4,
                    hour = 23,
                    minute = 0,
                    second = 0,
                    nanosecond = 0,
                ).toInstant(TimeZone.UTC),
                type = "Codelab",
                categories = listOf(categoriesTestData[2]),
            ),
            userData = userData,
        ),
        UserShopItem(
            shopItem = ShopItem(
                id = 2,
                title = "Thanks for helping us reach 1M YouTube Subscribers",
                description = "Thank you everyone for following the Now in Android series and everything the " +
                    "Android Developers YouTube channel has to offer. During the Android Developer " +
                    "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
                    "thank you all.",
                price = 15f,
                stock = 15,
                images = listOf("https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg"),
                publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
                type = "Video 📺",
                categories = categoriesTestData.take(2),
            ),
            userData = userData,
        ),
        UserShopItem(
            shopItem = ShopItem(
                id = 3,
                title = "Transformations and customisations in the Paging Library",
                description = "A demonstration of different operations that can be performed " +
                    "with Paging. Transformations like inserting separators, when to " +
                    "create a new pager, and customisation options for consuming " +
                    "PagingData.",
                price = 18f,
                stock = 18,
                images = listOf("https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg"),
                publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
                type = "Video 📺",
                categories = listOf(categoriesTestData[2]),
            ),
            userData = userData,
        ),
        UserShopItem(
            shopItem = ShopItem(
                id = 4,
                title = "New Jetpack Release",
                description = "New Jetpack release includes updates to libraries such as CameraX, Benchmark, and" +
                    "more!",
                price = 21f,
                stock = 21,
                images = emptyList(),
                publishDate = Instant.parse("2022-10-01T00:00:00.000Z"),
                type = "",
                categories = listOf(categoriesTestData[2]),
            ),
            userData = userData,
        ),
    )
}
