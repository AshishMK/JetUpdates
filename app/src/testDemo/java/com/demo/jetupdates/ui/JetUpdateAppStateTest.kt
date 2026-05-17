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

package com.demo.jetupdates.ui

import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation3.runtime.NavBackStack
import com.demo.jetupdates.core.navigation.NavigationState
import com.demo.jetupdates.core.navigation.Navigator
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.util.TestNetworkMonitor
import com.demo.jetupdates.core.testing.util.TestTimeZoneMonitor
import com.demo.jetupdates.feature.cart.api.navigation.CartNavKey
import com.demo.jetupdates.feature.chat.api.navigation.ChatNavKey
import com.demo.jetupdates.feature.store.api.navigation.StoreNavKey
import com.demo.jetupdates.feature.trending.api.navigation.TrendingNavKey
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * Tests [JUAppState].
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class JetUpdateAppStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Create the test dependencies.
    private val networkMonitor = TestNetworkMonitor()

    private val timeZoneMonitor = TestTimeZoneMonitor()

    private val userDataRepository = TestUserDataRepository()
    // UserDataRepository(TestShopRepository(), TestUserDataRepository())

    // Subject under test.
    private lateinit var state: JUAppState

    private fun testNavigationState() = NavigationState(
        startKey = StoreNavKey,
        topLevelStack = NavBackStack(StoreNavKey),
        subStacks = mapOf(
            StoreNavKey to NavBackStack(StoreNavKey),
            CartNavKey to NavBackStack(CartNavKey),
        ),
    )

    @Test
    fun juAppState_currentDestination() = runTest {
        val navigationState = testNavigationState()
        val navigator = Navigator(navigationState)

        composeTestRule.setContent {
            state = remember(navigationState) {
                JUAppState(
                    coroutineScope = backgroundScope,
                    networkMonitor = networkMonitor,
                    userDataRepository = userDataRepository,
                    timeZoneMonitor = timeZoneMonitor,
                    navigationState = navigationState,
                )
            }
        }

        assertEquals(StoreNavKey, state.navigationState.currentTopLevelKey)
        assertEquals(StoreNavKey, state.navigationState.currentKey)

        // Navigate to another destination once
        navigator.navigate(CartNavKey)

        composeTestRule.waitForIdle()

        assertEquals(CartNavKey, state.navigationState.currentTopLevelKey)
        assertEquals(CartNavKey, state.navigationState.currentKey)
    }

    @Test
    fun juAppState_destinations() = runTest {
        composeTestRule.setContent {
            state = rememberJUAppState(
                networkMonitor = networkMonitor,
                userDataRepository = userDataRepository,
                timeZoneMonitor = timeZoneMonitor,
            )
        }

        val navigationState = state.navigationState

        assertEquals(4, navigationState.topLevelKeys.size)
        assertEquals(
            setOf(StoreNavKey, CartNavKey, TrendingNavKey(null), ChatNavKey),
            navigationState.topLevelKeys,
        )
    }

    @Test
    fun juAppState_whenNetworkMonitorIsOffline_StateIsOffline() =
        runTest(UnconfinedTestDispatcher()) {
            composeTestRule.setContent {
                state = JUAppState(
                    coroutineScope = backgroundScope,
                    networkMonitor = networkMonitor,
                    userDataRepository = userDataRepository,
                    timeZoneMonitor = timeZoneMonitor,
                    navigationState = testNavigationState(),
                )
            }

            backgroundScope.launch { state.isOffline.collect() }
            networkMonitor.setConnected(false)
            assertEquals(
                true,
                state.isOffline.value,
            )
        }

    @Test
    fun juAppState_differentTZ_withTimeZoneMonitorChange() = runTest(UnconfinedTestDispatcher()) {
        composeTestRule.setContent {
            state = JUAppState(
                coroutineScope = backgroundScope,
                networkMonitor = networkMonitor,
                userDataRepository = userDataRepository,
                timeZoneMonitor = timeZoneMonitor,
                navigationState = testNavigationState(),
            )
        }
        val changedTz = TimeZone.of("Europe/Prague")
        backgroundScope.launch { state.currentTimeZone.collect() }
        timeZoneMonitor.setTimeZone(changedTz)
        assertEquals(
            changedTz,
            state.currentTimeZone.value,
        )
    }
}
