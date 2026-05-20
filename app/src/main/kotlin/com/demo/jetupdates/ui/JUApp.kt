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

import android.util.Log
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowWidthSizeClass
import com.demo.jetupdates.R
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.component.AppNavigationSuiteScaffold
import com.demo.jetupdates.core.designsystem.component.AppTopAppBar
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.core.navigation.Navigator
import com.demo.jetupdates.core.navigation.toEntries
import com.demo.jetupdates.core.ui.LocalSharedTransitionScope
import com.demo.jetupdates.core.ui.LocalSnackbarHostState
import com.demo.jetupdates.feature.cart.impl.navigation.cartEntry
import com.demo.jetupdates.feature.category.impl.navigation.categoryEntry
import com.demo.jetupdates.feature.chat.impl.navigation.chatEntry
import com.demo.jetupdates.feature.product.api.navigation.ProductNavKey
import com.demo.jetupdates.feature.product.impl.navigation.productEntry
import com.demo.jetupdates.feature.search.impl.navigation.searchEntry
import com.demo.jetupdates.feature.settings.impl.SettingsDialog
import com.demo.jetupdates.feature.store.api.navigation.StoreNavKey
import com.demo.jetupdates.feature.store.impl.navigation.storeEntry
import com.demo.jetupdates.feature.trending.impl.navigation.trendingEntry
import com.demo.jetupdates.navigation.TOP_LEVEL_NAV_ITEMS
import com.google.samples.apps.nowinandroid.feature.search.api.navigation.SearchNavKey
import com.demo.jetupdates.feature.settings.impl.R as settingsR

@Composable
fun JUApp(

    appState: JUAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val shouldShowCategoriesActionItem = appState.navigationState.currentTopLevelKey == StoreNavKey

    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    AppBackground(modifier = modifier) {
        val snackbarHostState = remember { SnackbarHostState() }

        val isOffline by appState.isOffline.collectAsStateWithLifecycle()

        // If user is not connected to the internet show a snack bar to inform them.
        val notConnectedMessage = stringResource(R.string.not_connected)
        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite,
                )
            }
        }
        val shouldShowOnboarding by appState.shouldShowOnboarding.collectAsStateWithLifecycle()
        LaunchedEffect(shouldShowOnboarding) {
            if (shouldShowOnboarding == 0) {
                appState.showCategoryList = false
            }
        }

        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            JUApp(
                appState = appState,
                showSettingsDialog = showSettingsDialog,
                onSettingsDismissed = { showSettingsDialog = false },
                onTopAppBarActionClick = { showSettingsDialog = true },
                onTopAppBarCategoryActionClick = {
                    appState.toggleCategoryList()
                },
                shouldShowCategoriesActionItem = shouldShowCategoriesActionItem,
                windowAdaptiveInfo = windowAdaptiveInfo,
            )
        }
    }
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3AdaptiveApi::class,
)
internal fun JUApp(
    appState: JUAppState,
    showSettingsDialog: Boolean,
    onSettingsDismissed: () -> Unit,
    onTopAppBarActionClick: () -> Unit,
    onTopAppBarCategoryActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    shouldShowCategoriesActionItem: Boolean,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    /* val unreadDestinations by appState.topLevelDestinationsWithUnreadResources
         .collectAsStateWithLifecycle()*/
    //  val currentDestination = appState.currentDestination

    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { onSettingsDismissed() },
        )
    }

    val snackbarHostState = LocalSnackbarHostState.current

    val navigator = remember { Navigator(appState.navigationState) }
    val hideBottomBar = appState.navigationState.currentKey !in appState.navigationState.topLevelKeys && windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    //val hideBottomBar = appState.navigationState.currentSubStack.last() is ProductNavKey && windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    Log.v("hideBottomBar", "hideBottomBar $hideBottomBar ${appState.navigationState.currentSubStack.last()}")
    // currentDestination?.hasRoute(route = ProductRoute::class) ?: false && windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    AppNavigationSuiteScaffold(
        hideBottomBar = hideBottomBar,
        navigationSuiteItems = {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                val hasUnread = false // unreadNavKeys.contains(navKey)
                val selected = navKey == appState.navigationState.currentTopLevelKey
                item(
                    selected = selected,
                    onClick = { navigator.navigate(navKey) },
                    icon = {
                        Icon(
                            imageVector = navItem.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = navItem.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(navItem.iconTextId)) },
                    modifier = Modifier
                        .testTag("AppNavItem"),
                    // .then(if (hasUnread) Modifier.notificationDot() else Modifier),
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        Scaffold(
            modifier = modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            /*  .exclude(WindowInsets.navigationBars)
              .exclude(WindowInsets.ime),*/

            snackbarHost = {
                SnackbarHost(
                    snackbarHostState,
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.exclude(
                            WindowInsets.ime,
                        ),
                    ),
                )
            },
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                // Show the top app bar on top level destinations.
                //   val destination = appState.currentTopLevelDestination
                var shouldShowTopAppBar = false

                //  if (destination != null) {
                if (appState.navigationState.currentKey in appState.navigationState.topLevelKeys) {
                    shouldShowTopAppBar = true

                    val destination = TOP_LEVEL_NAV_ITEMS[appState.navigationState.currentTopLevelKey]
                        ?: error("Top level nav item not found for ${appState.navigationState.currentTopLevelKey}")

                    AppTopAppBar(
                        titleRes = destination.titleTextId,
                        navigationIcon = AppIcons.Search,
                        navigationIconContentDescription = stringResource(
                            id = settingsR.string.feature_settings_impl_top_app_bar_navigation_icon_description,
                        ),
                        actionIcon = AppIcons.Settings,
                        actionIconContentDescription = stringResource(
                            id = settingsR.string.feature_settings_impl_top_app_bar_action_icon_description,
                        ),
                        actionIconCategories = AppIcons.Category,
                        actionIconCategoriesContentDescription = stringResource(
                            id = R.string.feature_categories_top_app_bar_navigation_icon_description,
                        ),
                        showCategoriesActionItem = shouldShowCategoriesActionItem,

                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                        ),

                        onNavigationClick = { navigator.navigate(SearchNavKey) },
                        onActionClick = { onTopAppBarActionClick() },
                        onCategoryActionClick = { onTopAppBarCategoryActionClick() },
                    )
                }

                Box(
                    // Workaround for https://issuetracker.google.com/338478720
                    modifier = Modifier.consumeWindowInsets(
                        if (shouldShowTopAppBar) {
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                        } else {
                            WindowInsets(0, 0, 0, 0)
                        },
                    ),
                ) {
                    /*  AppNavHost(
                          appState = appState,
                          onShowSnackbar = { message, action ->
                              snackbarHostState.showSnackbar(
                                  message = message,
                                  actionLabel = action,
                                  duration = Short,
                              ) == ActionPerformed
                          },
                          showCategoryList = showCategoryList,
                          clickedByUser = clickedByUser,
                          windowAdaptiveInfo = windowAdaptiveInfo,
                      )*/

                    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

                    val entryProvider = entryProvider {
                        storeEntry(
                            navigator = navigator,
                            showCategoryListProvider = { appState.showCategoryList },
                            clickedByUserProvider = { appState.clickedByUser },
                        )
                        cartEntry(navigator)
                        trendingEntry(navigator)
                        categoryEntry(navigator)
                        searchEntry(navigator)
                        chatEntry(navigator)
                        productEntry(navigator)
                    }

                    SharedTransitionLayout {
                        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                            NavDisplay(
                                entries = appState.navigationState.toEntries(entryProvider),
                                sceneStrategy = listDetailStrategy,
                                onBack = { navigator.goBack() },
                            )
                        }
                    }
                }

                // TODO: We may want to add padding or spacer when the snackbar is shown so that
                //  content doesn't display behind it.
            }
        }
    }
}

private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }
