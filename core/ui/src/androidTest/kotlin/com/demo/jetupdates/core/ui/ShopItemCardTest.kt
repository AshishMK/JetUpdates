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

package com.demo.jetupdates.core.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.demo.jetupdates.core.data.userShopItemsTestData
import org.junit.Rule
import org.junit.Test

class ShopItemCardTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testMetaDataDisplay_withCodelabResource() {
        val shopItemWithKnownResourceType = userShopItemsTestData[0]

        composeTestRule.setContent {
            ItemResourceCardForList2(
                userShopItem = shopItemWithKnownResourceType,
                isAddedToCart = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onCategoryClick = {},
            )
        }

        // assertEquals("1",1.toString())

        composeTestRule
            .onNodeWithText(
                shopItemWithKnownResourceType.title,
            )
            .assertExists()
    }

    @Test
    fun testAddedToCartCard_IconState() {
        val shopItemWithKnownResourceType = userShopItemsTestData[0]
        composeTestRule.setContent {
            ItemResourceCardForList2(
                userShopItem = shopItemWithKnownResourceType,
                isAddedToCart = shopItemWithKnownResourceType.isSaved,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onCategoryClick = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(R.string.core_ui_remove_from_cart),
            ).assertIsToggleable().assertExists()
    }

    /*    @Test
        fun testMetaDataDisplay_withCodelabResource() {
            val shopItemWithKnownResourceType = userShopItemsTestData[0]
            lateinit var dateFormatted: String

            composeTestRule.setContent {
                ItemResourceCardForList2(
                    userShopItem = shopItemWithKnownResourceType,
                    isAddedToCart = false,
                    hasBeenViewed = false,
                    onToggleBookmark = {},
                    onClick = {},
                    onCategoryClick = {},
                )

                dateFormatted = dateFormatted(publishDate = shopItemWithKnownResourceType.publishDate)
            }

            composeTestRule
                .onNodeWithText(
                    composeTestRule.activity.getString(
                        R.string.core_ui_card_meta_data_text,
                        dateFormatted,
                        shopItemWithKnownResourceType.type,
                    ),
                )
                .assertExists()
        }*/

    /*@Test
    fun testMetaDataDisplay_withEmptyResourceType() {
        val shopItemWithEmptyResourceType = userShopItemsTestData[3]
        lateinit var dateFormatted: String

        composeTestRule.setContent {
            ItemResourceCardForList2(
                userShopItem = shopItemWithEmptyResourceType,
                isAddedToCart = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onCategoryClick = {},
            )

            dateFormatted = dateFormatted(publishDate = shopItemWithEmptyResourceType.publishDate)
        }

        composeTestRule
            .onNodeWithText(dateFormatted)
            .assertIsDisplayed()
    }
*/
    /*@Test
    fun testCategoriesChipColorBackground_matchesFollowedState() {
        composeTestRule.setContent {
            ShopResourceCategories(
                categories = followableCategoryTestData,
                onCategoryClick = {},
            )
        }

        for (followableCategory in followableCategoryTestData) {
            val categoryName = followableCategory.category.name
            val expectedContentDescription = if (followableCategory.isFollowed) {
                "$tcategoryName is followed"
            } else {
                "$categoryName is not followed"
            }
            composeTestRule
                .onNodeWithText(categoryName.uppercase())
                .assertContentDescriptionEquals(expectedContentDescription)
        }
    }*/

    /*    @Test
        fun testUnreadDot_displayedWhenUnread() {
            val unreadShopItem = userShopItemsTestData[2]

            composeTestRule.setContent {
                ItemResourceCardForList2(
                    userShopItem = unreadShopItem,
                    isAddedToCart = false,
                    hasBeenViewed = false,
                    onToggleBookmark = {},
                    onClick = {},
                    onCategoryClick = {},
                )
            }

            composeTestRule
                .onNodeWithContentDescription(
                    composeTestRule.activity.getString(
                        R.string.core_ui_unread_resource_dot_content_description,
                    ),
                )
                .assertIsDisplayed()
        }

        @Test
        fun testUnreadDot_notDisplayedWhenRead() {
            val readShopItem = userShopItemsTestData[0]

            composeTestRule.setContent {
                ItemResourceCardForList2(
                    userShopItem = readShopItem,
                    isAddedToCart = false,
                    hasBeenViewed = true,
                    onToggleBookmark = {},
                    onClick = {},
                    onCategoryClick = {},
                )
            }

            composeTestRule
                .onNodeWithContentDescription(
                    composeTestRule.activity.getString(
                        R.string.core_ui_unread_resource_dot_content_description,
                    ),
                )
                .assertDoesNotExist()
        }*/
}
