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

package com.demo.jetupdates.feature.store

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.demo.jetUpdates.core.testing.util.DefaultTestDevices
import com.demo.jetUpdates.core.testing.util.captureForDevice
import com.demo.jetUpdates.core.testing.util.captureMultiDevice
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.ui.ItemFeedUiState
import com.demo.jetupdates.core.ui.ItemFeedUiState.Success
import com.demo.jetupdates.core.ui.UserShopResourcePreviewParameterProvider
import com.demo.jetupdates.feature.store.OnboardingUiState.Loading
import com.demo.jetupdates.feature.store.OnboardingUiState.NotShown
import com.demo.jetupdates.feature.store.OnboardingUiState.Shown
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesElements
import com.google.android.apps.common.testing.accessibility.framework.checks.TextContrastCheck
import com.google.android.apps.common.testing.accessibility.framework.matcher.ElementMatchers.withText
import dagger.hilt.android.testing.HiltTestApplication
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import java.util.TimeZone

/**
 * Screenshot tests for the [StoreScreen].
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class StoreScreenScreenshotTests {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val userShopItems = UserShopResourcePreviewParameterProvider().values.first()

    @Before
    fun setTimeZone() {
        // Make time zone deterministic in tests
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun storeScreenPopulatedFeed() {
        composeTestRule.captureMultiDevice("StoreScreenPopulatedFeed") {
            AppTheme {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState = NotShown,
                    feedState = Success(
                        feed = userShopItems,
                    ),
                    onCategoryCheckedChanged = { _, _ -> },
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onCategoryClick = {},
                    deepLinkedUserShopItem = null,
                    onDeepLinkOpened = {},
                    categoryActionClicked = {},
                    showCategoryList = false,
                )
            }
        }
    }

    @Test
    fun storeScreenLoading() {
        composeTestRule.captureMultiDevice("StoreScreenLoading") {
            AppTheme {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState = Loading,
                    feedState = ItemFeedUiState.Loading,
                    onCategoryCheckedChanged = { _, _ -> },
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onCategoryClick = {},
                    deepLinkedUserShopItem = null,
                    onDeepLinkOpened = {},
                    categoryActionClicked = {},
                    showCategoryList = false,
                )
            }
        }
    }

    @Test
    fun storeScreenCategorySelection() {
        composeTestRule.captureMultiDevice(
            "StoreScreenCategorySelection",
            accessibilitySuppressions = Matchers.allOf(
                AccessibilityCheckResultUtils.matchesCheck(TextContrastCheck::class.java),
                Matchers.anyOf(
                    // Disabled Button
                    matchesElements(withText("Done")),

                    // TODO investigate, seems a false positive
                    matchesElements(withText("What are you interested in?")),
                    matchesElements(withText("UI")),
                ),
            ),
        ) {
            StoreScreenCategorySelection()
        }
    }

    @Test
    fun storeScreenCategorySelection_dark() {
        composeTestRule.captureForDevice(
            deviceName = "phone_dark",
            deviceSpec = DefaultTestDevices.PHONE.spec,
            screenshotName = "StoreScreenCategorySelection",
            darkMode = true,
        ) {
            StoreScreenCategorySelection()
        }
    }

    @Test
    fun storeScreenPopulatedAndLoading() {
        composeTestRule.captureMultiDevice("StoreScreenPopulatedAndLoading") {
            StoreScreenPopulatedAndLoading()
        }
    }

    @Test
    fun storeScreenPopulatedAndLoading_dark() {
        composeTestRule.captureForDevice(
            deviceName = "phone_dark",
            deviceSpec = DefaultTestDevices.PHONE.spec,
            screenshotName = "StoreScreenPopulatedAndLoading",
            darkMode = true,
        ) {
            StoreScreenPopulatedAndLoading()
        }
    }

    @Composable
    private fun StoreScreenCategorySelection() {
        AppTheme {
            AppBackground {
                StoreScreen(
                    isSyncing = false,
                    onboardingUiState = Shown(
                        categories = userShopItems.flatMap { shopItem -> shopItem.followableCategories }
                            .distinctBy { it.category.id },
                        true,
                    ),
                    feedState = Success(
                        feed = emptyList(),
                    ),
                    onCategoryCheckedChanged = { _, _ -> },
                    saveFollowedCategories = {},
                    onShopItemCheckedChanged = { _, _ -> },
                    onShopItemViewed = {},
                    onCategoryClick = {},
                    deepLinkedUserShopItem = null,
                    onDeepLinkOpened = {},
                    showCategoryList = true,
                    categoryActionClicked = {},
                )
            }
        }
    }

    @Composable
    private fun StoreScreenPopulatedAndLoading() {
        AppTheme {
            AppBackground {
                AppTheme {
                    StoreScreen(
                        isSyncing = true,
                        onboardingUiState = Loading,
                        feedState = Success(
                            feed = userShopItems,
                        ),
                        onCategoryCheckedChanged = { _, _ -> },
                        saveFollowedCategories = {},
                        onShopItemCheckedChanged = { _, _ -> },
                        onShopItemViewed = {},
                        onCategoryClick = {},
                        deepLinkedUserShopItem = null,
                        onDeepLinkOpened = {},
                        showCategoryList = false,
                        categoryActionClicked = {},
                    )
                }
            }
        }
    }
}
