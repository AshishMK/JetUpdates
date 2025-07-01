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

package com.demo.jetupdates.trending

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.PowerCategory
import androidx.benchmark.macro.PowerCategoryDisplayLevel
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import com.demo.jetupdates.PACKAGE_NAME
import com.demo.jetupdates.allowNotifications
import com.demo.jetupdates.store.setAppTheme
import com.demo.jetupdates.store.storeScrollFeedDownUp
import com.demo.jetupdates.store.storeSelectCategories
import com.demo.jetupdates.store.storeWaitForContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMetricApi::class)
@RequiresApi(VERSION_CODES.Q)
@RunWith(AndroidJUnit4::class)
class ScrollCategoryListPowerMetricsBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val categories = PowerCategory.entries
        .associateWith { PowerCategoryDisplayLevel.TOTAL }

    @Test
    fun benchmarkStateChangeCompilationLight() =
        benchmarkStateChangeWithTheme(CompilationMode.Partial(), false)

    @Test
    fun benchmarkStateChangeCompilationDark() =
        benchmarkStateChangeWithTheme(CompilationMode.Partial(), true)

    private fun benchmarkStateChangeWithTheme(compilationMode: CompilationMode, isDark: Boolean) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), PowerMetric(PowerMetric.Energy(categories))),
            compilationMode = compilationMode,
            iterations = 2,
            startupMode = StartupMode.WARM,
            setupBlock = {
                // Start the app
                pressHome()
                startActivityAndWait()
                allowNotifications()
                // Navigate to Settings
                device.findObject(By.desc("Settings")).click()
                device.waitForIdle()
                setAppTheme(isDark)
            },
        ) {
            storeWaitForContent()
            storeSelectCategories()
            repeat(3) {
                storeScrollFeedDownUp()
            }
        }
}
