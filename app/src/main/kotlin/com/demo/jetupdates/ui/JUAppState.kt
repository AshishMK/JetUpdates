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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.data.util.NetworkMonitor
import com.demo.jetupdates.core.data.util.TimeZoneMonitor
import com.demo.jetupdates.core.navigation.NavigationState
import com.demo.jetupdates.core.navigation.rememberNavigationState
import com.demo.jetupdates.core.ui.TrackDisposableJank
import com.demo.jetupdates.feature.store.api.navigation.StoreNavKey
import com.demo.jetupdates.navigation.TOP_LEVEL_NAV_ITEMS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
@Composable
fun rememberJUAppState(
    networkMonitor: NetworkMonitor,
    userDataRepository: UserDataRepository,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),

): JUAppState {
//    NavigationTrackingSideEffect(navController)

    val navigationState = rememberNavigationState(StoreNavKey, TOP_LEVEL_NAV_ITEMS.keys)
    NavigationTrackingSideEffect(navigationState)

    return remember(
        navigationState,
        coroutineScope,
        networkMonitor,
        timeZoneMonitor,
    ) {
        JUAppState(
            userDataRepository = userDataRepository,
            timeZoneMonitor = timeZoneMonitor,
            navigationState = navigationState,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
        )
    }
}

@Stable
class JUAppState(
    userDataRepository: UserDataRepository,
    timeZoneMonitor: TimeZoneMonitor,
    networkMonitor: NetworkMonitor,
    val navigationState: NavigationState,
    coroutineScope: CoroutineScope,

) {
    var showCategoryList by mutableStateOf(true)

    // it works without being mutableStateOf because showCategoryList trigger recompose for same changes @see toggleCategoryList below
    var clickedByUser by mutableStateOf(false)

    fun toggleCategoryList() {
        showCategoryList = !showCategoryList
        clickedByUser = true
    }

    val shouldShowOnboarding = userDataRepository.userData.map { if (it.shouldHideOnboarding) 0 else 1 }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = -1,
        )

 /*   private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
        }*/

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    // val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * The top level destinations that have unread Shop Items.
     */
    // val topLevelDestinationsWithUnreadResources: StateFlow<Set<TopLevelDestination>> =
    /*        userShopItemRepository.observeAllForFollowedCategories()
                .combine(userShopItemRepository.observeAllBookmarked()) { storeShopItems, bookmarkedShopItems->
                    setOfNotNull(
                        STORE.takeIf { storeShopItems.any { !it.hasBeenViewed } },
                        BOOKMARKS.takeIf { bookmarkedShopItems.any { !it.hasBeenViewed } },
                    )
                }
                .stateIn(
                    coroutineScope,
                    SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptySet(),
                )*/

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            TimeZone.currentSystemDefault(),
        )
}

/**
 * Stores information about navigation events to be used with JankStats
 */
@Composable
private fun NavigationTrackingSideEffect(navigationState: NavigationState) {
    TrackDisposableJank(navigationState.currentKey) { metricsHolder ->
        metricsHolder.state?.putState("Navigation", navigationState.currentKey.toString())
        onDispose {}
    }
}
