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

package com.demo.jetupdates.cart

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.demo.jetupdates.waitForObjectOnTopAppBar

fun MacrobenchmarkScope.goToCartScreen() {
// needed for baseline profile generation in ci/cd @[NightlyBaselineProfiles.yaml]
    cartWaitForCategories()
    val savedSelector = By.text("Cart")
    val savedButton = device.findObject(savedSelector)
    savedButton.click()
    device.waitForIdle()
    // Wait until saved title are shown on screen
    waitForObjectOnTopAppBar(savedSelector)
}
fun MacrobenchmarkScope.cartWaitForCategories() { // may need later
    device.wait(Until.hasObject(By.text("Random")), 5_000)
}
