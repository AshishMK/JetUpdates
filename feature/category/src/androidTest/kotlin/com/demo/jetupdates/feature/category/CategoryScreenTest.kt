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

package com.demo.jetupdates.feature.category

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.demo.jetupdates.core.testing.data.followableCategoryTestData
import com.demo.jetupdates.core.testing.data.userShopItemsTestData
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.core.ui.LocalSharedTransitionScope
import com.demo.jetupdates.feature.category.CategoryUiState.Loading
import com.demo.jetupdates.feature.category.CategoryUiState.Success
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI test for checking the correct behaviour of the Topic screen;
 * Verifies that, when a specific UiState is set, the corresponding
 * composables and details are shown
 */
@OptIn(ExperimentalSharedTransitionApi::class)
class CategoryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var categoryLoading: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            categoryLoading = getString(R.string.feature_category_loading)
        }
    }

    @Test
    fun appLoadingWheel_whenScreenIsLoading_showLoading() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        CategoryScreen(
                            categoryUiState = Loading,
                            shopItemUiState = ShopItemUiState.Loading,
                            showBackButton = true,
                            onBackClick = {},
                            onFollowClick = {},
                            onProductClick = {},
                            onBookmarkChanged = { _, _ -> },
                            onShopItemViewed = {},
                        )
                    }
                }
            }
        }

        composeTestRule
            .onNodeWithContentDescription(categoryLoading)
            .assertExists()
    }

    @Test
    fun topicTitle_whenCategoryIsSuccess_isShown() {
        val testCategory = followableCategoryTestData.first()
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        CategoryScreen(
                            categoryUiState = Success(testCategory),
                            shopItemUiState = ShopItemUiState.Loading,
                            showBackButton = true,
                            onBackClick = {},
                            onFollowClick = {},
                            onProductClick = {},
                            onBookmarkChanged = { _, _ -> },
                            onShopItemViewed = {},
                        )
                    }
                }
            }
        }

        // Name is shown
        composeTestRule
            .onNodeWithText(testCategory.category.name)
            .assertExists()

        // Description is shown
        composeTestRule
            .onNodeWithText(testCategory.category.longDescription)
            .assertExists()
    }

    @Test
    fun news_whenCategoryIsLoading_isNotShown() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        CategoryScreen(
                            categoryUiState = Loading,
                            shopItemUiState = ShopItemUiState.Success(userShopItemsTestData),
                            showBackButton = true,
                            onBackClick = {},
                            onFollowClick = {},
                            onProductClick = {},
                            onBookmarkChanged = { _, _ -> },
                            onShopItemViewed = {},
                        )
                    }
                }
            }
        }

        // Loading indicator shown
        composeTestRule
            .onNodeWithContentDescription(categoryLoading)
            .assertExists()
    }

    @Test
    fun news_whenSuccessAndCategoryIsSuccess_isShown() {
        val testCategory = followableCategoryTestData.first()
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        CategoryScreen(
                            categoryUiState = Success(testCategory),
                            shopItemUiState = ShopItemUiState.Success(
                                userShopItemsTestData,
                            ),
                            showBackButton = true,
                            onBackClick = {},
                            onFollowClick = {},
                            onProductClick = {},
                            onBookmarkChanged = { _, _ -> },
                            onShopItemViewed = {},
                        )
                    }
                }
            }
        }

        // Scroll to first news title if available
        composeTestRule
            .onAllNodes(hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(hasText(userShopItemsTestData.first().title))
    }
}
