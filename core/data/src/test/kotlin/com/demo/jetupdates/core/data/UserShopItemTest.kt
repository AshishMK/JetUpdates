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

package com.demo.jetupdates.core.data

import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.DarkThemeConfig
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.model.data.ThemeBrand
import com.demo.jetupdates.core.model.data.UserData
import com.demo.jetupdates.core.model.data.UserShopItem
import kotlinx.datetime.Clock
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserShopItemTest {

    /**
     * Given: Some user data and shop items
     * When: They are combined using `UserShopItem.from`
     * Then: The correct UserShopItems are constructed
     */
    @Test
    fun userNewsResourcesAreConstructedFromNewsResourcesAndUserData() {
        val newsResource1 = ShopItem(
            id = 1,
            title = "Test news title",
            price = 34.50f,
            description = "Test news content",
            stock = 110,
            images = listOf("https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg"),
            publishDate = Clock.System.now(),
            type = "Article 📚",
            categories = listOf(
                Category(
                    id = 1,
                    name = "Topic 1",
                    shortDescription = "Topic 1 short description",
                    longDescription = "Topic 1 long description",
                    url = "Topic 1 URL",
                    imageUrl = "Topic 1 image URL",
                ),
                Category(
                    id = 2,
                    name = "Topic 2",
                    shortDescription = "Topic 2 short description",
                    longDescription = "Topic 2 long description",
                    url = "Topic 2 URL",
                    imageUrl = "Topic 2 image URL",
                ),
            ),
        )

        val userData = UserData(
            bookmarkedShopItems = setOf(1),
            viewedShopItems = setOf(1),
            followedCategories = setOf(1),
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = true,
        )

        val userNewsResource = UserShopItem(newsResource1, userData)

        // Check that the simple field mappings have been done correctly.
        assertEquals(newsResource1.id, userNewsResource.id)
        assertEquals(newsResource1.title, userNewsResource.title)
        assertEquals(newsResource1.description, userNewsResource.description)
        assertEquals(newsResource1.stock, userNewsResource.stock)
        assertEquals(newsResource1.price, userNewsResource.price)
        assertEquals(newsResource1.publishDate, userNewsResource.publishDate)

        assertEquals(newsResource1.images.size, userNewsResource.images.size)
        for (image in newsResource1.images) {
            assertTrue(userNewsResource.images.contains(image))
        }
        // Check that each Category has been converted to a FollowableCategory correctly.
        assertEquals(newsResource1.categories.size, userNewsResource.followableCategories.size)
        for (category in newsResource1.categories) {
            // Construct the expected FollowableCategory.
            val followableCategory = FollowableCategory2(
                category = category,
                isFollowed = category.id in userData.followedCategories,
            )
            assertTrue(userNewsResource.followableCategories.contains(followableCategory))
        }

        // Check that the saved flag is set correctly.
        assertEquals(
            newsResource1.id in userData.bookmarkedShopItems,
            userNewsResource.isSaved,
        )
    }
}
