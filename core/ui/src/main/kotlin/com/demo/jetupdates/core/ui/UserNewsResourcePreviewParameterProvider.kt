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

package com.demo.jetupdates.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.DarkThemeConfig
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.model.data.ThemeBrand
import com.demo.jetupdates.core.model.data.UserData
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.ui.PreviewParameterData.shopItems
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [UserShopItem] for Composable previews.
 */
class UserShopResourcePreviewParameterProvider : PreviewParameterProvider<List<UserShopItem>> {

    override val values: Sequence<List<UserShopItem>> = sequenceOf(shopItems)
}

object PreviewParameterData {

    private val userData: UserData = UserData(
        bookmarkedShopItems = setOf(1, 3),
        viewedShopItems = setOf(1, 2, 4),
        followedCategories = emptySet(),
        themeBrand = ThemeBrand.ANDROID,
        darkThemeConfig = DarkThemeConfig.DARK,
        shouldHideOnboarding = true,
        useDynamicColor = false,
    )

    val categories = listOf(
        Category(
            id = 2,
            name = "Headlines",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 3,
            name = "UI",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 4,
            name = "Testing",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 5,
            name = "COMPOSE",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 6,
            name = "KOTLIN",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 7,
            name = "ARCHITECTURE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 7,
            name = "GAMES",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 8,
            name = "PERFORMANCE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),

        Category(
            id = 9,
            name = "Headlines",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 10,
            name = "UI",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 11,
            name = "Testing",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 12,
            name = "COMPOSE",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 13,
            name = "KOTLIN",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 14,
            name = "ARCHITECTURE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 15,
            name = "GAMES",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 16,
            name = "PERFORMANCE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),

        Category(
            id = 17,
            name = "Headlines",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 18,
            name = "UI",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 19,
            name = "Testing",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 20,
            name = "COMPOSE",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 21,
            name = "KOTLIN",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 22,
            name = "ARCHITECTURE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 23,
            name = "GAMES",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 24,
            name = "PERFORMANCE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),

        Category(
            id = 25,
            name = "Headlines",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 26,
            name = "UI",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 27,
            name = "Testing",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 28,
            name = "COMPOSE",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Category(
            id = 29,
            name = "KOTLIN",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 30,
            name = "ARCHITECTURE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
        Category(
            id = 31,
            name = "GAMES",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Category(
            id = 32,
            name = "PERFORMANCE",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
    )

    val shopItems = listOf(
        UserShopItem(
            shopItem = ShopItem(
                id = 1,
                title = "Luxury Face Cream",
                price = 212.35f,
                description = "We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. You’ll learn the fundamentals of programming in Kotlin while building Android apps using Jetpack Compose, Android’s modern toolkit that simplifies and accelerates native UI development. These two units are just the beginning; more will be coming soon. Check out Android Basics with Compose to get started on your Android development journey",
                stock = 123,
                images = listOf("https://od.lk/d/NzlfNTQxMjE3ODZf/prod_50.jpg", "https://od.lk/d/NzlfNTQxMjE3ODVf/prod_49.jpg"),
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
                categories = listOf(categories[2]),
            ),
            userData = userData,
        ),
        UserShopItem(
            shopItem = ShopItem(
                id = 2,
                title = "Stylish Travel Mug",
                price = 349.16f,
                description = "Thank you everyone for following the Now in Android series and everything the " +
                    "Android Developers YouTube channel has to offer. During the Android Developer " +
                    "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
                    "thank you all.",
                stock = 159,
                images = listOf(
                    "https://od.lk/d/NzlfNTQxMjE3ODRf/prod_48.jpg",
                    "https://od.lk/d/NzlfNTQxMjE3ODNf/prod_47.jpg",
                ),
                publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
                type = "Video 📺",
                categories = categories.take(2),
            ),
            userData = userData,
        ),
        UserShopItem(
            shopItem = ShopItem(
                id = 3,
                title = "Handcrafted Sneakers",
                price = 40.45f,
                description = "A demonstration of different operations that can be performed " +
                    "with Paging. Transformations like inserting separators, when to " +
                    "create a new pager, and customisation options for consuming " +
                    "PagingData.",
                stock = 84,
                images = listOf(
                    "https://od.lk/d/NzlfNTQxMjE3ODJf/prod_46.jpg",
                    "https://od.lk/d/NzlfNTQxMjE3ODFf/prod_45.jpg",
                ),
                publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
                type = "Video 📺",
                categories = listOf(categories[2]),
            ),
            userData = userData,
        ),
        UserShopItem(
            shopItem = ShopItem(
                id = 4,
                title = "Portable Scented Candle Set",
                price = 111.6f,
                description = "The portable scented candle set is provides superior comfort, efficiency, and reliability across all tasks. Featuring a thoughtfully designed structure, this product is ideal for those seeking quality and reliability. With attention to small details and a commitment to user satisfaction, it is sure to become a favorite in your daily essentials.",
                stock = 26,
                images = listOf(
                    "https://od.lk/d/NzlfNTQxMjE3ODBf/prod_44.jpg",
                    "https://od.lk/d/NzlfNTQxMjE3Nzlf/prod_43.jpg",
                ),
                publishDate = Instant.parse("2022-10-06T23:00:00.000Z"),
                type = "Video 📺",
                categories = listOf(categories[2]),
            ),
            userData = userData,
        ),
    )
}
