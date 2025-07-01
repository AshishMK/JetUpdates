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

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.demo.jetupdates.R
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.feature.cart.navigation.CartRoute
import com.demo.jetupdates.feature.chat.navigation.ChatRoute
import com.demo.jetupdates.feature.store.navigation.StoreBaseRoute
import com.demo.jetupdates.feature.store.navigation.StoreRoute
import com.demo.jetupdates.feature.trending.navigation.TrendingRoute
import kotlin.reflect.KClass
import com.demo.jetupdates.feature.cart.R as cartR
import com.demo.jetupdates.feature.chat.R as chatR
import com.demo.jetupdates.feature.search.R as searchR
import com.demo.jetupdates.feature.store.R as forYouR

/**
 * Type for the top level destinations in the application. Contains metadata about the destination
 * that is used in the top app bar and common navigation UI.
 *
 * @param selectedIcon The icon to be displayed in the navigation UI when this destination is
 * selected.
 * @param unselectedIcon The icon to be displayed in the navigation UI when this destination is
 * not selected.
 * @param iconTextId Text that to be displayed in the navigation UI.
 * @param titleTextId Text that is displayed on the top app bar.
 * @param route The route to use when navigating to this destination.
 * @param baseRoute The highest ancestor of this destination. Defaults to [route], meaning that
 * there is a single destination in that section of the app (no nested destinations).
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    STORE(
        selectedIcon = AppIcons.Store,
        unselectedIcon = AppIcons.StoreBorder,
        iconTextId = forYouR.string.feature_store_title,
        titleTextId = R.string.app_name,
        route = StoreRoute::class,
        baseRoute = StoreBaseRoute::class,
    ),
    CART(
        selectedIcon = AppIcons.Cart,
        unselectedIcon = AppIcons.CartBorder,
        iconTextId = cartR.string.feature_cart_title,
        titleTextId = cartR.string.feature_cart_title,
        route = CartRoute::class,
    ),
    TRENDING(
        selectedIcon = AppIcons.Fire,
        unselectedIcon = AppIcons.FireBorder,
        iconTextId = searchR.string.feature_search_trending,
        titleTextId = searchR.string.feature_search_trending,
        route = TrendingRoute::class,
    ),
    CHAT(
        selectedIcon = AppIcons.Chat,
        unselectedIcon = AppIcons.ChatBorder,
        iconTextId = chatR.string.feature_chat_title,
        titleTextId = chatR.string.feature_chat_title,
        route = ChatRoute::class,
    ),
}
