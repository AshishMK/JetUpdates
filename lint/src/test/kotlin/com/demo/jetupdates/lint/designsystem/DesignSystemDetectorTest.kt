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

package com.demo.jetupdates.lint.designsystem

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.demo.jetupdates.lint.designsystem.DesignSystemDetector.Companion.ISSUE
import com.demo.jetupdates.lint.designsystem.DesignSystemDetector.Companion.METHOD_NAMES
import com.demo.jetupdates.lint.designsystem.DesignSystemDetector.Companion.RECEIVER_NAMES
import org.junit.Test

class DesignSystemDetectorTest {

    @Test
    fun `detect replacements of Composable`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                @Suppress("LintImplTrimIndent")
                kotlin(
                    """
                    |import androidx.compose.runtime.Composable
                    |
                    |@Composable
                    |fun App() {
                    ${METHOD_NAMES.keys.joinToString("\n") { "|    $it()" }}
                    |}
                    """.trimMargin(),
                ).indented(),
            )
            .run()
            .expect(
                """
                src/test.kt:5: Error: Using MaterialTheme instead of AppTheme [DesignSystem]
                    MaterialTheme()
                    ~~~~~~~~~~~~~~~
                src/test.kt:6: Error: Using Button instead of AppButton [DesignSystem]
                    Button()
                    ~~~~~~~~
                src/test.kt:7: Error: Using OutlinedButton instead of AppOutlinedButton [DesignSystem]
                    OutlinedButton()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:8: Error: Using TextButton instead of AppTextButton [DesignSystem]
                    TextButton()
                    ~~~~~~~~~~~~
                src/test.kt:9: Error: Using FilterChip instead of AppFilterChip [DesignSystem]
                    FilterChip()
                    ~~~~~~~~~~~~
                src/test.kt:10: Error: Using ElevatedFilterChip instead of AppFilterChip [DesignSystem]
                    ElevatedFilterChip()
                    ~~~~~~~~~~~~~~~~~~~~
                src/test.kt:11: Error: Using NavigationBar instead of AppNavigationBar [DesignSystem]
                    NavigationBar()
                    ~~~~~~~~~~~~~~~
                src/test.kt:12: Error: Using NavigationBarItem instead of AppNavigationBarItem [DesignSystem]
                    NavigationBarItem()
                    ~~~~~~~~~~~~~~~~~~~
                src/test.kt:13: Error: Using NavigationRail instead of AppNavigationRail [DesignSystem]
                    NavigationRail()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:14: Error: Using NavigationRailItem instead of AppNavigationRailItem [DesignSystem]
                    NavigationRailItem()
                    ~~~~~~~~~~~~~~~~~~~~
                src/test.kt:15: Error: Using TabRow instead of AppTabRow [DesignSystem]
                    TabRow()
                    ~~~~~~~~
                src/test.kt:16: Error: Using Tab instead of AppTab [DesignSystem]
                    Tab()
                    ~~~~~
                src/test.kt:17: Error: Using IconToggleButton instead of AppIconToggleButton [DesignSystem]
                    IconToggleButton()
                    ~~~~~~~~~~~~~~~~~~
                src/test.kt:18: Error: Using FilledIconToggleButton instead of AppIconToggleButton [DesignSystem]
                    FilledIconToggleButton()
                    ~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:19: Error: Using FilledTonalIconToggleButton instead of AppIconToggleButton [DesignSystem]
                    FilledTonalIconToggleButton()
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:20: Error: Using OutlinedIconToggleButton instead of AppIconToggleButton [DesignSystem]
                    OutlinedIconToggleButton()
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:21: Error: Using CenterAlignedTopAppBar instead of AppTopAppBar [DesignSystem]
                    CenterAlignedTopAppBar()
                    ~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:22: Error: Using SmallTopAppBar instead of AppTopAppBar [DesignSystem]
                    SmallTopAppBar()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:23: Error: Using MediumTopAppBar instead of AppTopAppBar [DesignSystem]
                    MediumTopAppBar()
                    ~~~~~~~~~~~~~~~~~
                src/test.kt:24: Error: Using LargeTopAppBar instead of AppTopAppBar [DesignSystem]
                    LargeTopAppBar()
                    ~~~~~~~~~~~~~~~~
                20 errors, 0 warnings
                """.trimIndent(),
            )
    }

    @Test
    fun `detect replacements of Receiver`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                @Suppress("LintImplTrimIndent")
                kotlin(
                    """
                    |fun main() {
                    ${RECEIVER_NAMES.keys.joinToString("\n") { "|    $it.toString()" }}
                    |}
                    """.trimMargin(),
                ).indented(),
            )
            .run()
            .expect(
                """
                src/test.kt:2: Error: Using Icons instead of AppIcons [DesignSystem]
                    Icons.toString()
                    ~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent(),
            )
    }

    private companion object {

        private val COMPOSABLE_STUB: TestFile = kotlin(
            """
            package androidx.compose.runtime
            annotation class Composable
            """.trimIndent(),
        ).indented()

        private val STUBS: TestFile = kotlin(
            """
            |import androidx.compose.runtime.Composable
            |
            ${METHOD_NAMES.keys.joinToString("\n") { "|@Composable fun $it() = {}" }}
            ${RECEIVER_NAMES.keys.joinToString("\n") { "|object $it" }}
            |
            """.trimMargin(),
        ).indented()
    }
}
