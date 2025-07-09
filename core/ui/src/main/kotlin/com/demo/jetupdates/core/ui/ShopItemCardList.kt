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

package com.demo.jetupdates.core.ui

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.demo.jetupdates.core.model.data.UserShopItem

/**
 * Extension function for displaying a [List] of [ShopItemCardExpanded] backed by a list of
 * [UserShopItem]s.
 *
 * [onToggleBookmark] defines the action invoked when a user wishes to bookmark an item
 * When a news resource card is tapped it will open the news resource URL in a Chrome Custom Tab.
 */
fun LazyStaggeredGridScope.userShopItemCardItems(
    items: List<UserShopItem>,
    onToggleBookmark: (item: UserShopItem) -> Unit,
    onShopItemViewed: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit,
    itemModifier: Modifier = Modifier,
) = items(
    items = items,
    key = { it.id },
    itemContent = { userShopItem ->
        val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
        val context = LocalContext.current
        // val analyticsHelper = LocalAnalyticsHelper.current

        ItemResourceCardForList2(
            userShopItem = userShopItem,
            isAddedToCart = userShopItem.isSaved,
            hasBeenViewed = userShopItem.hasBeenViewed,
            onToggleBookmark = { onToggleBookmark(userShopItem) },
            onClick = {
                /* analyticsHelper.logNewsResourceOpened(
                     newsResourceId = userShopItem.id,
                 )*/
                onShopItemViewed(userShopItem.id)
                onCategoryClick(userShopItem.id)
            },
            modifier = itemModifier,
        )
    },
)
