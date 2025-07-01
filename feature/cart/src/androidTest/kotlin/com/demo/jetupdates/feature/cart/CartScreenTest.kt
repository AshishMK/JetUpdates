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

package com.demo.jetupdates.feature.cart

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import com.demo.jetupdates.core.testing.data.userShopItemsTestData
import com.demo.jetupdates.core.ui.ItemFeedUiState
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * UI tests for [CartScreen] composable.
 */
class CartScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loading_showsLoadingSpinner() {
        composeTestRule.setContent {
            CartScreen(
                feedState = ItemFeedUiState.Loading,
                onShowSnackbar = { _, _ -> false },
                removeFromCart = {},
                onTopicClick = {},
                onShopItemViewed = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_cart_loading),
            )
            .assertExists()
    }

    @Test
    fun feed_whenHasItems_showsItems() {
        composeTestRule.setContent {
            CartScreen(
                feedState = ItemFeedUiState.Success(
                    userShopItemsTestData.take(2),
                ),
                onShowSnackbar = { _, _ -> false },
                removeFromCart = {},
                onTopicClick = {},
                onShopItemViewed = {},
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

    @Test
    fun feed_whenRemovingItem_removesItem() {
        var removeFromCartCalled = false

        composeTestRule.setContent {
            CartScreen(
                feedState = ItemFeedUiState.Success(
                    userShopItemsTestData.take(2),
                ),
                onShowSnackbar = { _, _ -> false },
                removeFromCart = { shopItemId ->
                    assertEquals(userShopItemsTestData[0].id, shopItemId)
                    removeFromCartCalled = true
                },
                onTopicClick = {},
                onShopItemViewed = {},
            )
        }

        composeTestRule
            .onAllNodesWithContentDescription(
                composeTestRule.activity.getString(
                    com.demo.jetupdates.core.ui.R.string.core_ui_unbookmark,
                ),
            ).filter(
                hasAnyAncestor(
                    hasText(
                        userShopItemsTestData[0].title,
                        substring = true,
                    ),
                ),
            )
            .assertCountEquals(1)
            .onFirst()
            .performClick()

        assertTrue(removeFromCartCalled)
    }

    @Test
    fun feed_whenHasNoItems_showsEmptyState() {
        composeTestRule.setContent {
            CartScreen(
                feedState = ItemFeedUiState.Success(emptyList()),
                onShowSnackbar = { _, _ -> false },
                removeFromCart = {},
                onTopicClick = {},
                onShopItemViewed = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_cart_empty_error),
            )
            .assertExists()

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.feature_cart_empty_description),
            )
            .assertExists()
    }

    @Test
    fun feed_whenLifecycleStops_undoCartStateIsCleared() = runTest {
        var undoStateCleared = false
        val testLifecycleOwner = TestLifecycleOwner(initialState = Lifecycle.State.STARTED)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides testLifecycleOwner) {
                CartScreen(
                    feedState = ItemFeedUiState.Success(emptyList()),
                    onShowSnackbar = { _, _ -> false },
                    removeFromCart = {},
                    onTopicClick = {},
                    onShopItemViewed = {},
                    clearUndoState = {
                        undoStateCleared = true
                    },
                )
            }
        }

        assertEquals(false, undoStateCleared)
        testLifecycleOwner.handleLifecycleEvent(event = Lifecycle.Event.ON_STOP)
        assertEquals(true, undoStateCleared)
    }
}
