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

package com.demo.jetupdates.trending

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.demo.jetupdates.flingElementDownUp
import com.demo.jetupdates.waitForObjectOnTopAppBar

fun MacrobenchmarkScope.goToTrendingScreen() {
    device.findObject(By.text("Trending")).click()
    device.waitForIdle()
    // Wait until interests are shown on screen
    waitForObjectOnTopAppBar(By.text("Trending"))

    // Wait until content is loaded by checking if interests are loaded
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
}

fun MacrobenchmarkScope.trendingScrollCategoriesDownUp() {
    device.wait(Until.hasObject(By.res("trending:categories")), 5_000)
    val categoriesList = device.findObject(By.res("trending:categories"))
    device.flingElementDownUp(categoriesList)
}

fun MacrobenchmarkScope.trendingWaitForCategories() {
    device.wait(Until.hasObject(By.text("Accessibility")), 30_000)
}

fun MacrobenchmarkScope.trendingToggleAddedToCart() {
    val categoriesList = device.findObject(By.res("trending:categories"))
    val checkable = categoriesList.findObject(By.checkable(true))
    checkable.click()
    device.waitForIdle()
}
