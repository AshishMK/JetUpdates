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

package com.demo.jetupdates.feature.store

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.demo.jetupdates.core.data.followableCategoryTestData
import com.demo.jetupdates.core.data.userShopItemsTestData
import com.demo.jetupdates.core.rules.GrantPostNotificationsPermissionRule
import com.demo.jetupdates.core.ui.ItemFeedUiState
import org.junit.Rule
import org.junit.Test

class StoreScreenTest {

    @get:Rule(order = 0)
    val postNotificationsPermission = GrantPostNotificationsPermissionRule()

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val doneButtonMatcher by lazy {
        hasText(
            composeTestRule.activity.resources.getString(R.string.feature_store_done),
        )
    }

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            Box {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState = OnboardingUiState.Loading,
                    feedState = ItemFeedUiState.Loading,
                    deepLinkedUserShopItem = null,
                    onCategoryCheckedChanged = { _, _ -> },
                    onCategoryClick = {},
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onDeepLinkOpened = {},
                    categoryActionClicked = {},
                    showCategoryList = false,
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_store_loading),
            )
            .assertExists()
    }

    @Test
    fun circularProgressIndicator_whenScreenIsSyncing_exists() {
        composeTestRule.setContent {
            Box {
                StoreScreen(
                    isSyncing = true,
                    onboardingUiState = OnboardingUiState.NotShown,
                    feedState = ItemFeedUiState.Success(emptyList()),
                    deepLinkedUserShopItem = null,
                    onCategoryCheckedChanged = { _, _ -> },
                    onCategoryClick = {},
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onDeepLinkOpened = {},
                    showCategoryList = false,
                    categoryActionClicked = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_store_loading),
            )
            .assertExists()
    }

    @Test
    fun categorySelector_whenNoCategoriesSelected_showsCategoryChipsAndDisabledDoneButton() {
        val testData = followableCategoryTestData.map { it.copy(isFollowed = false) }

        composeTestRule.setContent {
            Box {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState = OnboardingUiState.Shown(
                        categories = testData,
                        shouldShowOnboarding = true,
                    ),
                    feedState = ItemFeedUiState.Success(
                        feed = emptyList(),
                    ),
                    deepLinkedUserShopItem = null,
                    onCategoryCheckedChanged = { _, _ -> },
                    onCategoryClick = {},
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onDeepLinkOpened = {},
                    showCategoryList = true,
                    categoryActionClicked = {},
                )
            }
        }

        testData.forEach { testCategory ->
            composeTestRule
                .onNodeWithText(testCategory.category.name)
                .assertExists()
                .assertHasClickAction()
        }

        // Scroll until the Done button is visible
        composeTestRule
            .onAllNodes(hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(doneButtonMatcher)

        composeTestRule
            .onNode(doneButtonMatcher)
            .assertExists()
            .assertIsNotEnabled()
            .assertHasClickAction()
    }

    @Test
    fun categorySelector_whenSomeCategoriesSelected_showsCategoryChipsAndEnabledDoneButton() {
        composeTestRule.setContent {
            Box {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState =
                    OnboardingUiState.Shown(
                        // Follow one category
                        categories = followableCategoryTestData.mapIndexed { index, testCategory ->
                            testCategory.copy(isFollowed = index == 1)
                        },
                        shouldShowOnboarding = true,
                    ),
                    feedState = ItemFeedUiState.Success(
                        feed = emptyList(),
                    ),
                    deepLinkedUserShopItem = null,
                    onCategoryCheckedChanged = { _, _ -> },
                    onCategoryClick = {},
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onDeepLinkOpened = {},
                    showCategoryList = true,
                    categoryActionClicked = {},
                )
            }
        }

        followableCategoryTestData.forEach { testCategory ->
            Log.v("yuyy", "yuyy ${testCategory.category.name}")
            composeTestRule
                .onNodeWithText(testCategory.category.name)
                .assertExists()
                .assertHasClickAction()
        }

        // Scroll until the Done button is visible
        composeTestRule
            .onAllNodes(hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(doneButtonMatcher)

        composeTestRule
            .onNode(doneButtonMatcher)
            .assertExists()
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun feed_whenInterestsSelectedAndLoading_showsLoadingIndicator() {
        composeTestRule.setContent {
            Box {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState =
                    OnboardingUiState.Shown(
                        categories = followableCategoryTestData,
                        shouldShowOnboarding = true,
                    ),
                    feedState = ItemFeedUiState.Loading,
                    deepLinkedUserShopItem = null,
                    onCategoryCheckedChanged = { _, _ -> },
                    onCategoryClick = {},
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onDeepLinkOpened = {},
                    showCategoryList = false,
                    categoryActionClicked = { },
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_store_loading),
            )
            .assertExists()
    }

    @Test
    fun feed_whenNoInterestsSelectionAndLoading_showsLoadingIndicator() {
        composeTestRule.setContent {
            Box {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState = OnboardingUiState.NotShown,
                    feedState = ItemFeedUiState.Loading,
                    deepLinkedUserShopItem = null,
                    onCategoryCheckedChanged = { _, _ -> },
                    onCategoryClick = {},
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onDeepLinkOpened = {},
                    showCategoryList = false,
                    categoryActionClicked = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_store_loading),
            )
            .assertExists()
    }

    @Test
    fun feed_whenNoInterestsSelectionAndLoaded_showsFeed() {
        composeTestRule.setContent {
            StoreScreen(
                isSyncing = false,
                onboardingUiState = OnboardingUiState.NotShown,
                feedState = ItemFeedUiState.Success(
                    feed = userShopItemsTestData,
                ),
                deepLinkedUserShopItem = null,
                onCategoryCheckedChanged = { _, _ -> },
                onCategoryClick = {},
                saveFollowedCategories = {},
                onShopItemCheckedChanged = { _, _ -> },
                onShopItemViewed = {},
                onDeepLinkOpened = {},
                showCategoryList = false,
                categoryActionClicked = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                userShopItemsTestData[0].title,
                substring = true,
            )
            .assertExists()
            .assertHasClickAction()

        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(
                hasText(
                    userShopItemsTestData[1].title,
                    substring = true,
                ),
            )

        composeTestRule
            .onNodeWithText(
                userShopItemsTestData[1].title,
                substring = true,
            )
            .assertExists()
            .assertHasClickAction()
    }
}
