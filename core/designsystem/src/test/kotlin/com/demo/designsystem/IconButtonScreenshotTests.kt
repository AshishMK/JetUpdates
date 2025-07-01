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

import androidx.activity.ComponentActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.demo.jetUpdates.core.testing.util.captureMultiTheme
import com.demo.jetupdates.core.designsystem.component.AppIconButton
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class IconButtonScreenshotTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun iconButton_multipleThemes() {
        composeTestRule.captureMultiTheme("IconButton") {
            appIconButtonExample(enabled = true, transparentBackground = false)
        }
    }

    @Test
    fun iconButton_disabled_multipleThemes() {
        composeTestRule.captureMultiTheme("IconButton", "IconButtonUnchecked") {
            Surface {
                appIconButtonExample(enabled = false, transparentBackground = false)
            }
        }
    }

    @Test
    fun iconButton_transparent_background_multipleThemes() {
        composeTestRule.captureMultiTheme("IconButton", "IconButtonTransparent") {
            Surface {
                appIconButtonExample(enabled = true, transparentBackground = true)
            }
        }
    }

    @Composable
    private fun appIconButtonExample(enabled: Boolean, transparentBackground: Boolean) {
        AppIconButton(
            enabled = enabled,
            onClick = { },
            icon = {
                Icon(
                    imageVector = AppIcons.Emoji,
                    contentDescription = null,
                )
            },
            transparentBackground = transparentBackground,
        )
    }
}
