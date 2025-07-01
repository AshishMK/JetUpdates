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

package com.demo.jetupdates.trending

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.demo.jetupdates.core.testing.data.followableCategoryTestData
import com.demo.jetupdates.feature.trending.TrendingScreen
import com.demo.jetupdates.feature.trending.TrendingUiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.demo.jetupdates.core.ui.R as CoreUiR
import com.demo.jetupdates.feature.trending.R as TrendingR

/**
 * UI test for checking the correct behaviour of the Interests screen;
 * Verifies that, when a specific UiState is set, the corresponding
 * composables and details are shown
 */
class TrendingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var trendingLoading: String
    private lateinit var trendingEmptyHeader: String
    private lateinit var trendingCategoryCardFollowButton: String
    private lateinit var trendingCategoryCardUnfollowButton: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            trendingLoading = getString(TrendingR.string.feature_trending_loading)
            trendingEmptyHeader = getString(TrendingR.string.feature_trending_empty_header)
            trendingCategoryCardFollowButton =
                getString(CoreUiR.string.core_ui_trending_card_follow_button_content_desc)
            trendingCategoryCardUnfollowButton =
                getString(CoreUiR.string.core_ui_trending_card_unfollow_button_content_desc)
        }
    }

    @Test
    fun niaLoadingWheel_inCategories_whenScreenIsLoading_showLoading() {
        composeTestRule.setContent {
            TrendingScreen(uiState = TrendingUiState.Loading)
        }

        composeTestRule
            .onNodeWithContentDescription(trendingLoading)
            .assertExists()
    }

    @Test
    fun trendingWithCategories_whenCategoriesFollowed_showFollowedAndUnfollowedCategoriesWithInfo() {
        composeTestRule.setContent {
            TrendingScreen(
                uiState = TrendingUiState.Trending(
                    categories = followableCategoryTestData,
                    selectedCategoryId = null,
                ),
            )
        }

        composeTestRule
            .onNodeWithText(followableCategoryTestData[0].category.name)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(followableCategoryTestData[1].category.name)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(followableCategoryTestData[2].category.name)
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithContentDescription(trendingCategoryCardFollowButton)
            .assertCountEquals(numberOfUnfollowedCategories)
    }

    @Test
    fun categoriesEmpty_whenDataIsEmptyOccurs_thenShowEmptyScreen() {
        composeTestRule.setContent {
            TrendingScreen(uiState = TrendingUiState.Empty)
        }

        composeTestRule
            .onNodeWithText(trendingEmptyHeader)
            .assertIsDisplayed()
    }

    @Composable
    private fun TrendingScreen(uiState: TrendingUiState) {
        TrendingScreen(
            uiState = uiState,
            followCategory = { _, _ -> },
            onCategoryClick = {},
        )
    }
}

private val numberOfUnfollowedCategories = followableCategoryTestData.filter { !it.isFollowed }.size
