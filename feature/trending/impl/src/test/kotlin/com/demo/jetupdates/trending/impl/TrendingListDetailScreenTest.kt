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

@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.demo.jetupdates.trending.impl

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.espresso.Espresso
import com.demo.jetupdates.core.data.repository.CategoriesRepository
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.navigation.Navigator
import com.demo.jetupdates.core.navigation.rememberNavigationState
import com.demo.jetupdates.core.navigation.toEntries
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.core.ui.LocalSharedTransitionScope
import com.demo.jetupdates.feature.category.impl.navigation.categoryEntry
import com.demo.jetupdates.feature.trending.api.R
import com.demo.jetupdates.feature.trending.api.navigation.TrendingNavKey
import com.demo.jetupdates.feature.trending.impl.LIST_PANE_TEST_TAG
import com.demo.jetupdates.feature.trending.impl.navigation.trendingEntry
import com.demo.jetupdates.uitesthiltmanifest.HiltComponentActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import kotlin.getValue
import kotlin.properties.ReadOnlyProperty

private const val EXPANDED_WIDTH = "w1200dp-h840dp"
private const val COMPACT_WIDTH = "w412dp-h915dp"

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [35])
class TrendingListDetailScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

    @Inject
    lateinit var categoryRepository: CategoriesRepository

    /** Convenience function for getting all topics during tests, */
    private fun getCategorys(): List<Category> = runBlocking {
        categoryRepository.getCategories().first().sortedBy { it.name }
    }

    // The strings used for matching in these tests.
    private val placeholderText by composeTestRule.stringResource(R.string.feature_trending_api_select_an_trending)

    private val Category.testTag
        get() = "category:${this.id}"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_initialState_showsTwoPanesWithPlaceholder() {
        composeTestRule.apply {
            setContent {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                            LocalNavAnimatedVisibilityScope provides this,
                        ) {
                            AppTheme {
                                TestNavDisplay()
                            }
                        }
                    }
                }
            }
            onNodeWithTag(LIST_PANE_TEST_TAG).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsDisplayed()
        }
    }

    @Composable
    private fun TestNavDisplay() {
        val startKey = TrendingNavKey(null)

        val navigationState = rememberNavigationState(
            startKey = startKey,
            topLevelKeys = setOf(startKey),
        )

        val navigator = Navigator(navigationState)

        val entryProvider = entryProvider {
            trendingEntry(navigator)
            categoryEntry(navigator)
        }

        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategy = rememberListDetailSceneStrategy(),
        )
    }

    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_initialState_showsListPane() {
        composeTestRule.apply {
            setContent {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                            LocalNavAnimatedVisibilityScope provides this,
                        ) {
                            AppTheme {
                                TestNavDisplay()
                            }
                        }
                    }
                }
            }

            onNodeWithTag(LIST_PANE_TEST_TAG).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
        }
    }

    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_topicSelected_updatesDetailPane() {
        composeTestRule.apply {
            setContent {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                            LocalNavAnimatedVisibilityScope provides this,
                        ) {
                            AppTheme {
                                TestNavDisplay()
                            }
                        }
                    }
                }
            }
            val firstCategory = getCategorys().first()
            onNodeWithText(firstCategory.name).performClick()
            waitForIdle()

            onNodeWithTag(LIST_PANE_TEST_TAG).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstCategory.testTag).assertIsDisplayed()
        }
    }

    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_topicSelected_showsCategoryDetailPane() {
        composeTestRule.apply {
            setContent {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                            LocalNavAnimatedVisibilityScope provides this,
                        ) {
                            AppTheme {
                                TestNavDisplay()
                            }
                        }
                    }
                }
            }

            val firstCategory = getCategorys().first()
            onNodeWithText(firstCategory.name).performClick()

            onNodeWithTag(LIST_PANE_TEST_TAG).assertIsNotDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstCategory.testTag).assertIsDisplayed()
        }
    }

    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_backPressFromCategoryDetail_showsListPane() {
        composeTestRule.apply {
            setContent {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                            LocalNavAnimatedVisibilityScope provides this,
                        ) {
                            AppTheme {
                                TestNavDisplay()
                            }
                        }
                    }
                }
            }

            val firstCategory = getCategorys().first()
            onNodeWithText(firstCategory.name).performClick()

            waitForIdle()
            Espresso.pressBack()

            onNodeWithTag(LIST_PANE_TEST_TAG).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstCategory.testTag).assertIsNotDisplayed()
        }
    }
}

private fun AndroidComposeTestRule<*, *>.stringResource(
    @StringRes resId: Int,
): ReadOnlyProperty<Any, String> =
    ReadOnlyProperty { _, _ -> activity.getString(resId) }
