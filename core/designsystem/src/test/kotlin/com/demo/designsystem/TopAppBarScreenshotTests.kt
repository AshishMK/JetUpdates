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

package com.demo.designsystem

import android.R
import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.demo.jetUpdates.core.testing.util.DefaultRoborazziOptions
import com.demo.jetUpdates.core.testing.util.captureMultiTheme
import com.demo.jetupdates.core.designsystem.component.AppTopAppBar
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class TopAppBarScreenshotTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun topAppBar_multipleThemes() {
        composeTestRule.captureMultiTheme("TopAppBar") {
            AppTopAppBarExample(showCategoriesActionItem = true)
        }
    }

    @Test
    fun topAppBar_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    DeviceConfigurationOverride.FontScale(2f),
                ) {
                    AppTheme {
                        AppTopAppBarExample(showCategoriesActionItem = true)
                    }
                }
            }
        }

        composeTestRule.onRoot()
            .captureRoboImage(
                "src/test/screenshots/TopAppBar/TopAppBar_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    @Test
    fun topAppBar_hide_categoryIcon_multipleThemes() {
        composeTestRule.captureMultiTheme("TopAppBa_No_Category") {
            AppTopAppBarExample(showCategoriesActionItem = false)
        }
    }

    @Composable
    private fun AppTopAppBarExample(showCategoriesActionItem: Boolean) {
        AppTopAppBar(
            titleRes = R.string.untitled,
            navigationIcon = AppIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = AppIcons.MoreVert,
            actionIconContentDescription = "Action icon",
            actionIconCategories = AppIcons.Category,
            actionIconCategoriesContentDescription = "Action Icon",
            showCategoriesActionItem = showCategoriesActionItem,
        )
    }
}
