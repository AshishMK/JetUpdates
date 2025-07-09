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

package com.demo.jetupdates.feature.product

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipe
import androidx.window.core.layout.WindowWidthSizeClass
import com.demo.jetupdates.core.testing.data.userShopItemsTestData
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.core.ui.LocalSharedTransitionScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.demo.jetupdates.core.ui.R.string as ProductString

@OptIn(ExperimentalSharedTransitionApi::class)
class ProductScreenTest {
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

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var addToCart: String
    private lateinit var removeFromCart: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            addToCart = getString(ProductString.core_ui_add_to_cart)
            removeFromCart = getString(ProductString.core_ui_remove_from_cart)
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun productTitleDescription_whenProductIsSuccess_isShown() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        ProductScreen(
                            productUiState = userShopItemsTestData[0],
                            id = userShopItemsTestData[0].id,
                            onBackClick = {},
                            onCartChanged = {},
                            windowAdaptiveInfo = currentWindowAdaptiveInfo(),
                        )
                    }
                }
            }
        }

        // Name is shown
        composeTestRule
            .onNodeWithText(userShopItemsTestData[0].title)
            .assertIsDisplayed()

        // Description is shown
        composeTestRule
            .onNodeWithText(userShopItemsTestData[0].description, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun productFAB_whenScreenIsScrolled_isExpanded() {
        var isCompact = true
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        isCompact =
                            currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
                        ProductScreen(
                            productUiState = userShopItemsTestData[0],
                            id = userShopItemsTestData[0].id,
                            onBackClick = {},
                            onCartChanged = {},
                            windowAdaptiveInfo = currentWindowAdaptiveInfo(),
                        )
                    }
                }
            }
        }
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")
        // FAB is shown see rally project for use of semantics tree compose notebook 87
        composeTestRule.onNode(
            hasContentDescription(if (isCompact) "Product_FAB_Collapsed" else "Product_FAB_Expanded") and hasTestTag(
                "Product_FAB",
            ),
            useUnmergedTree = true,
        ).assertIsDisplayed()
        // composeTestRule.onNode(  hasContentDescription(removeFromCart) and hasParent(hasTestTag("Product_FAB")),useUnmergedTree = true).assertIsDisplayed()
        /* .onNodeWithTag("Product_FAB").assertContentDescriptionEquals("h")
         .assertIsDisplayed()*/
        composeTestRule.onNodeWithTag("Scroll_Product_Details").performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y - 500),
                durationMillis = 200,
            )
        }

        composeTestRule.onNode(
            hasContentDescription("Product_FAB_Expanded") and hasTestTag(
                "Product_FAB",
            ),
            useUnmergedTree = true,
        ).assertIsDisplayed()

        /*  composeTestRule.waitUntil(4000) {
                false
            }*/

        // FAB is Expanded
        // composeTestRule.onNode(hasText("Cart", substring = true) ).assertExists()
        /*.(if(userShopItemsTestData[0].isSaved) removeFromCart else removeFromCart)
        .assertIsDisplayed()*/
    }

    @Test
    fun whenAdd_remove_from_cart_toggleItemInCart() {
        var changeCartCalled = false

        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        ProductScreen(
                            productUiState = userShopItemsTestData[0],
                            id = userShopItemsTestData[0].id,
                            onBackClick = {},
                            onCartChanged = { addedToCart ->
                                // check isSaved toggled after click on FAB
                                assertEquals(userShopItemsTestData[0].isSaved, !addedToCart)
                                changeCartCalled = true
                            },
                            windowAdaptiveInfo = currentWindowAdaptiveInfo(),
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("Product_FAB").performClick()
        assertTrue(changeCartCalled)
    }
}
