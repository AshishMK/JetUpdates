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

package com.demo.jetupdates.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.demo.jetupdates.feature.cart.navigation.cartScreen
import com.demo.jetupdates.feature.chat.navigation.chatScreen
import com.demo.jetupdates.feature.search.navigation.searchScreen
import com.demo.jetupdates.feature.store.navigation.StoreBaseRoute
import com.demo.jetupdates.feature.store.navigation.storeSection
import com.demo.jetupdates.feature.trending.navigation.navigateToTrending
import com.demo.jetupdates.navigation.TopLevelDestination.TRENDING
import com.demo.jetupdates.ui.JUAppState
import com.demo.jetupdates.ui.trending2pane.trendingListDetailScreen

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun AppNavHost(
    appState: JUAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    showCategoryList: Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = StoreBaseRoute,
        modifier = modifier,
    ) {
        storeSection(onCategoryClick = {}, showCategoryList)
        cartScreen(
            onTopicClick = { _ -> },
            onShowSnackbar = onShowSnackbar,
        )
        searchScreen(
            onBackClick = navController::popBackStack,
            onTrendingClick = { appState.navigateToTopLevelDestination(TRENDING) },
            onCategoryClick = navController::navigateToTrending,
        )
        trendingListDetailScreen()

        chatScreen(
            onShowSnackbar = onShowSnackbar,
        )
    }
}
