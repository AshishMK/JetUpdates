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

package com.demo.jetupdates.chat

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.demo.jetupdates.flingElementDownUp
import com.demo.jetupdates.waitForObjectOnTopAppBar

fun MacrobenchmarkScope.goToChatScreen() {
    device.findObject(By.text("Chat")).click()
    device.waitForIdle()
    // Wait until interests are shown on screen
    waitForObjectOnTopAppBar(By.text("Chat"))

    // Wait until content is loaded by checking if interests are loaded
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
}

fun MacrobenchmarkScope.chatWaitForCategories() { // may need later
    device.wait(Until.hasObject(By.text("Random")), 10_000)
}
fun MacrobenchmarkScope.chatScrollCategoriesDownUp() {
    device.wait(Until.hasObject(By.res("chat:messages")), 5_000)
    val chatList = device.findObject(By.res("chat:messages"))
    device.flingElementDownUp(chatList)
}
