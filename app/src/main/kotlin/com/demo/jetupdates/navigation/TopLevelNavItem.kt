/*
 * Copyright 2025 The Android Open Source Project
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
import com.demo.jetupdates.feature.cart.api.navigation.CartNavKey
import com.demo.jetupdates.feature.chat.api.navigation.ChatNavKey
import com.demo.jetupdates.feature.store.api.navigation.StoreNavKey
import com.demo.jetupdates.feature.trending.api.navigation.TrendingNavKey
import com.demo.jetupdates.feature.cart.api.R as cartR
import com.demo.jetupdates.feature.chat.api.R as chatR
import com.demo.jetupdates.feature.store.api.R as storeR
import com.demo.jetupdates.feature.trending.api.R as trendingR

/**
 * Type for the top level navigation items in the application. Contains UI information about the
 * current route that is used in the top app bar and common navigation UI.
 *
 * @param selectedIcon The icon to be displayed in the navigation UI when this destination is
 * selected.
 * @param unselectedIcon The icon to be displayed in the navigation UI when this destination is
 * not selected.
 * @param iconTextId Text that to be displayed in the navigation UI.
 * @param titleTextId Text that is displayed on the top app bar.
 */
data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
)
val STORE = TopLevelNavItem(
    selectedIcon = AppIcons.Store,
    unselectedIcon = AppIcons.StoreBorder,
    iconTextId = storeR.string.feature_store_api_title,
    titleTextId = R.string.app_name,
)

val Cart = TopLevelNavItem(
    selectedIcon = AppIcons.Cart,
    unselectedIcon = AppIcons.CartBorder,
    iconTextId = cartR.string.feature_cart_api_title,
    titleTextId = cartR.string.feature_cart_api_title,
)

val TRENDING = TopLevelNavItem(
    selectedIcon = AppIcons.Fire,
    unselectedIcon = AppIcons.FireBorder,
    iconTextId = trendingR.string.feature_trending_api_title,
    titleTextId = trendingR.string.feature_trending_api_title,
)

val CHAT = TopLevelNavItem(
    selectedIcon = AppIcons.Chat,
    unselectedIcon = AppIcons.ChatBorder,
    iconTextId = chatR.string.feature_chat_api_title,
    titleTextId = chatR.string.feature_chat_api_title,
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    StoreNavKey to STORE,
    CartNavKey to Cart,
    TrendingNavKey(null) to TRENDING,
    ChatNavKey to CHAT,
)
