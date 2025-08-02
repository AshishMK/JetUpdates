/*
 * Copyright 2025 The Android Open Source Project
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

import com.demo.jetupdates.core.data.repository.CompositeUserShopItemRepository
import com.demo.jetupdates.core.data.repository.ShopItemQuery
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.model.data.mapToUserShopItems
import com.demo.jetupdates.core.testing.repository.TestShopRepository
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.repository.emptyUserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class CompositeUserShopItemRepositoryTest {

    private val shopRepository = TestShopRepository()
    private val userDataRepository = TestUserDataRepository()

    private val userShopItemRepository = CompositeUserShopItemRepository(
        shopRepository = shopRepository,
        userDataRepository = userDataRepository,
    )

    @Test
    fun whenNoFilters_allShopItemsAreReturned() = runTest {
        // Obtain the user shop items flow.
        val userShopItems = userShopItemRepository.observeAll()

        // Send some shop items and user data into the data repositories.
        shopRepository.sendShopItems(sampleShopItems)

        // Construct the test user data with bookmarks and followed categories.
        val userData =
            emptyUserData.copy(
                bookmarkedShopItems = setOf(
                    sampleShopItems[0].id,
                    sampleShopItems[2].id,
                ),
                followedCategories = setOf(sampleCategory1.id),
            )

        userDataRepository.setUserData(userData)

        // Check that the correct shop items are returned with their bookmarked state.
        assertEquals(
            sampleShopItems.mapToUserShopItems(
                userData,
            ),
            userShopItems.first(),
        )
    }

    @Test
    fun whenFilteredByCategoryId_matchingShopItemsAreReturned() = runTest {
        // Obtain a stream of user shop items for the given category id.
        val userShopItems =
            userShopItemRepository.observeAll(
                ShopItemQuery(
                    filterCategoryIds = setOf(
                        sampleCategory1.id,
                    ),
                ),
            )

        // Send test data into the repositories.
        shopRepository.sendShopItems(sampleShopItems)
        userDataRepository.setUserData(emptyUserData)

        // Check that only shop items with the given category id are returned.
        assertEquals(
            sampleShopItems
                .filter { sampleCategory1 in it.categories }
                .mapToUserShopItems(emptyUserData),
            userShopItems.first(),
        )
    }

    @Test
    fun whenGivenItemId_matchingShopItemIsReturned() = runTest {
        // Obtain a stream of user shop item for the given category id.
        val userShopItem =
            userShopItemRepository.observeItem(
                1,
            )

        // Send test data into the repositories.
        shopRepository.sendShopItems(sampleShopItems)
        userDataRepository.setUserData(emptyUserData)

        // Check that only shop items with the given category id are returned.
        assertEquals(
            UserShopItem(sampleShopItems[0], emptyUserData).id,
            userShopItem.first().id,
        )
    }

    @Test
    fun whenFilteredByFollowedCategories_matchingShopItemsAreReturned() = runTest {
        // Obtain a stream of user shop items for the given category id.
        val userShopItems =
            userShopItemRepository.observeAllForFollowedCategories()

        // Send test data into the repositories.
        val userData = emptyUserData.copy(
            followedCategories = setOf(sampleCategory1.id),
        )
        shopRepository.sendShopItems(sampleShopItems)
        userDataRepository.setUserData(userData)

        // Check that only shop items with the given category id are returned.
        assertEquals(
            sampleShopItems
                .filter { sampleCategory1 in it.categories }
                .mapToUserShopItems(userData),
            userShopItems.first(),
        )
    }

    @Test
    fun whenFilteredByBookmarkedResources_matchingShopItemsAreReturned() = runTest {
        // Obtain the bookmarked user shop items flow.
        val userShopItems = userShopItemRepository.observeAllBookmarked()

        // Send some shop items and user data into the data repositories.
        shopRepository.sendShopItems(sampleShopItems)

        // Construct the test user data with bookmarks and followed categories.
        val userData = emptyUserData.copy(
            bookmarkedShopItems = setOf(
                sampleShopItems[0].id,
                sampleShopItems[2].id,
            ),
            followedCategories = setOf(sampleCategory1.id),
        )

        userDataRepository.setUserData(userData)

        // Check that the correct shop items are returned with their bookmarked state.
        assertEquals(
            listOf(
                sampleShopItems[0],
                sampleShopItems[2],
            ).mapToUserShopItems(userData),
            userShopItems.first(),
        )
    }
}

private val sampleCategory1 = Category(
    id = 1,
    name = "Headlines",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

private val sampleCategory2 = Category(
    id = 2,
    name = "UI",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

private val sampleShopItems = listOf(
    ShopItem(
        id = 1,
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        price = 34.50f,
        description = "Thank you everyone for following the Now in Android series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        stock = 110,
        images = listOf("https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg"),
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(sampleCategory1),
    ),
    ShopItem(
        id = 2,
        title = "Transformations and customisations in the Paging Library",
        price = 329f,
        description = "A demonstration of different operations that can be performed with Paging. " +
            "Transformations like inserting separators, when to create a new pager, and " +
            "customisation options for consuming PagingData.",
        stock = 89,
        images = listOf("https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg"),
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(sampleCategory1, sampleCategory2),
    ),
    ShopItem(
        id = 3,
        title = "Community tip on Paging",
        price = 123f,
        description = "Tips for using the Paging library from the developer community",
        stock = 345,
        images = listOf("https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg"),
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(sampleCategory2),
    ),
)
