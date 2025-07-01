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

package com.demo.jetupdates.feature.chat

import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.ui.MessagePreviewParameterData.initialMessages
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.MatcherAssert
import org.hamcrest.text.IsEqualIgnoringCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * UI tests for [ChatScreen] composable.
 */
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /*    @Test
        fun loading_showsLoadingSpinner() {
            composeTestRule.setContent {
                ChatScreen(
                    feedState = initialMessages,
                    onShowSnackbar = { _, _ -> false },
                )
            }

            composeTestRule
                .onNodeWithContentDescription(
                    composeTestRule.activity.resources.getString(R.string.feature_bookmarks_loading),
                )
                .assertExists()
        }*/
    private val themeIsDark = MutableStateFlow(false)

    @Before
    fun setUp() {
        // Launch the conversation screen
        composeTestRule.setContent {
            AppTheme(darkTheme = themeIsDark.collectAsStateWithLifecycle(false).value) {
                AppBackground {
                    ChatScreen(
                        feedState = initialMessages,
                        onShowSnackbar = { _, _ -> false },
                    )
                }
            }
        }
    }

    @Test
    fun feed_shows_scroll_on_reverseLayout() {
        composeTestRule
            .onNodeWithText(
                initialMessages.first().content,
                substring = true,
            )
            .assertExists()

        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(
                hasText(
                    initialMessages.last().content,
                    substring = true,
                ),
            )

        composeTestRule
            .onNodeWithText(
                initialMessages.last().content,
                substring = true,
            )
            .assertExists()

        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()
    }

    @Test
    fun feed_only_distinct_author_messages_item_has_avatar() {
        assertNotEquals(initialMessages[1].author, initialMessages[2].author)
        // or
        assertTrue(initialMessages[1].author.equals(initialMessages[0].author.uppercase(), true))

        MatcherAssert.assertThat(
            initialMessages[1].author,
            IsEqualIgnoringCase.equalToIgnoringCase(initialMessages[0].author.uppercase()),
        )
        composeTestRule
            .onNodeWithContentDescription(
                initialMessages[1].content,
            )
            .assertExists().assertIsDisplayed()

        assertEquals(initialMessages[1].author, initialMessages[0].author)

        composeTestRule
            .onNodeWithContentDescription(
                initialMessages[0].content,
            )
            .assertDoesNotExist()
    }

    @Test
    fun userScrollsUp_jumpToBottomAppears() {
        // Check list is snapped to bottom and swipe up
        findJumpToBottom().assertDoesNotExist()
        composeTestRule.onNodeWithTag(CONVERSATION_TEST_TAG).performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200,
            )
        }
        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()
    }

    @Test
    fun jumpToBottom_snapsToBottomAndDisappears() {
        // When the scroll is not snapped to the bottom
        composeTestRule.onNodeWithTag(CONVERSATION_TEST_TAG).performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200,
            )
        }
        // Snap scroll to the bottom
        findJumpToBottom().performClick()

        // Check that the button is hidden
        findJumpToBottom().assertDoesNotExist()
    }

    @Test
    fun jumpToBottom_snapsToBottomAfterUserInteracted() {
        // First swipe
        composeTestRule.onNodeWithTag(
            testTag = CONVERSATION_TEST_TAG,
            useUnmergedTree = true, // https://issuetracker.google.com/issues/184825850
        ).performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200,
            )
        }
        // Second, snap to bottom
        findJumpToBottom().performClick()

        // Open Emoji selector
        openEmojiSelector()

        // Assert that the list is still snapped to bottom
        findJumpToBottom().assertDoesNotExist()
    }

    @Test
    fun changeTheme_scrollIsPersisted() {
        // Swipe to show the jump to bottom button
        composeTestRule.onNodeWithTag(CONVERSATION_TEST_TAG).performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200,
            )
        }

        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()
        // composeTestRule.waitUntil(4000) {

        // Set theme to dark
        themeIsDark.value = true

        // Check that the jump to bottom button is still shown
        findJumpToBottom().assertIsDisplayed()
        //    false
        // }
    }

    private fun findJumpToBottom() =
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(com.demo.jetupdates.core.ui.R.string.core_ui_jumpBottom),
            useUnmergedTree = true,
        )

    private fun openEmojiSelector() =
        composeTestRule
            .onNodeWithTag(
                composeTestRule.activity.getString(com.demo.jetupdates.core.ui.R.string.core_ui_emoji_selector_bt_desc),
            )
            .performClick()
}
