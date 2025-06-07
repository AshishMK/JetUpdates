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

package com.demo.jetupdates.store

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import com.demo.jetupdates.flingElementDownUp
import com.demo.jetupdates.waitAndFindObject
import com.demo.jetupdates.waitForObjectOnTopAppBar
import org.junit.Assert.fail

fun MacrobenchmarkScope.storeWaitForContent() {
    // Wait until content is loaded by checking if categories are loaded
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
    // Sometimes, the loading wheel is gone, but the content is not loaded yet
    // So we'll wait here for categories to be sure
    val obj = device.waitAndFindObject(By.res("store:categorySelection"), 10_000)
    // Timeout here is quite big, because sometimes data loading takes a long time!
    obj.wait(untilHasChildren(), 60_000)
}

/**
 * Selects some categories, which will show the feed content for them.
 * [recheckCategoriesIfChecked] Categories may be already checked from the previous iteration.
 */
fun MacrobenchmarkScope.storeSelectCategories(recheckCategoriesIfChecked: Boolean = false) {
    val categories = device.findObject(By.res("store:categorySelection"))

    // Set gesture margin from sides not to trigger system gesture navigation
    val horizontalMargin = 10 * categories.visibleBounds.width() / 100
    categories.setGestureMargins(horizontalMargin, 0, horizontalMargin, 0)

    // Select some categories to show some feed content
    var index = 0
    var visited = 0

    while (visited < 3) {
        if (categories.childCount == 0) {
            fail("No categories found, can't generate profile for Store page.")
        }
        // Selecting some categories, which will populate items in the feed.
        val category = categories.children[index % categories.childCount]
        // Find the checkable element to figure out whether it's checked or not
        val categoryCheckIcon = category.findObject(By.checkable(true))
        // Category icon may not be visible if it's out of the screen boundaries
        // If that's the case, let's try another index
        if (categoryCheckIcon == null) {
            index++
            continue
        }

        when {
            // Category wasn't checked, so just do that
            !categoryCheckIcon.isChecked -> {
                category.click()
                device.waitForIdle()
            }

            // Category was checked already and we want to recheck it, so just do it twice
            recheckCategoriesIfChecked -> {
                repeat(2) {
                    category.click()
                    device.waitForIdle()
                }
            }

            else -> {
                // Category is checked, but we don't recheck it
            }
        }

        index++
        visited++
    }
}

fun MacrobenchmarkScope.storeScrollFeedDownUp() {
    val feedList = device.findObject(By.res("store:feed"))
    device.flingElementDownUp(feedList)
}

fun MacrobenchmarkScope.setAppTheme(isDark: Boolean) {
    when (isDark) {
        true -> device.findObject(By.text("Dark")).click()
        false -> device.findObject(By.text("Light")).click()
    }
    device.waitForIdle()
    device.findObject(By.text("OK")).click()

    // Wait until the top app bar is visible on screen
    waitForObjectOnTopAppBar(By.text("JetUpdates"))
}
