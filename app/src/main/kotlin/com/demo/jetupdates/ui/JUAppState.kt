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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.data.util.NetworkMonitor
import com.demo.jetupdates.core.data.util.TimeZoneMonitor
import com.demo.jetupdates.core.ui.TrackDisposableJank
import com.demo.jetupdates.feature.cart.navigation.navigateToCart
import com.demo.jetupdates.feature.chat.navigation.navigateToChat
import com.demo.jetupdates.feature.search.navigation.navigateToSearch
import com.demo.jetupdates.feature.store.navigation.navigateToStore
import com.demo.jetupdates.feature.trending.navigation.navigateToTrending
import com.demo.jetupdates.navigation.TopLevelDestination
import com.demo.jetupdates.navigation.TopLevelDestination.CART
import com.demo.jetupdates.navigation.TopLevelDestination.CHAT
import com.demo.jetupdates.navigation.TopLevelDestination.STORE
import com.demo.jetupdates.navigation.TopLevelDestination.TRENDING
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
    navController: NavHostController = rememberNavController(),

): JUAppState {
    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
        timeZoneMonitor,
    ) {
        JUAppState(
            userDataRepository = userDataRepository,
            timeZoneMonitor = timeZoneMonitor,
            navController = navController,
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
    val navController: NavHostController,
    coroutineScope: CoroutineScope,

) {
    val shouldShowOnboarding = userDataRepository.userData.map { if (it.shouldHideOnboarding) 0 else 1 }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = -1,
        )

    private val previousDestination = mutableStateOf<NavDestination?>(null)

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
        }

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
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

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

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            when (topLevelDestination) {
                STORE -> navController.navigateToStore(topLevelNavOptions)
                CART -> navController.navigateToCart(topLevelNavOptions)
                TRENDING -> navController.navigateToTrending(null, topLevelNavOptions)
                CHAT -> navController.navigateToChat(topLevelNavOptions)
            }
        }
    }

    fun navigateToSearch() = navController.navigateToSearch()
}

/**
 * Stores information about navigation events to be used with JankStats
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    TrackDisposableJank(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
