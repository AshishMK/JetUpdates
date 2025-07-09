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

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells.Adaptive
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.UserShopItem

/**
 * An extension on [LazyListScope] defining a feed with shop items.
 * Depending on the [feedState], this might emit no items.
 */
fun LazyStaggeredGridScope.itemFeed(
    feedState: ItemFeedUiState,
    onShopItemCheckedChanged: (Int, Boolean) -> Unit,
    onShopItemViewed: (Int) -> Unit,
    onProductClick: (Int) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        ItemFeedUiState.Loading -> Unit
        is ItemFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "itemFeedItem" },
            ) { userShopItem ->
                val context = LocalContext.current
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

                ItemResourceCardForList2(
                    userShopItem = userShopItem,
                    isAddedToCart = userShopItem.isSaved,
                    onClick = {
                        onExpandedCardClick()
                        // launchCustomChromeTab(context, Uri.parse(userShopItem.url), backgroundColor)
                        onShopItemViewed(userShopItem.id)
                        onProductClick(userShopItem.id)
                    },
                    hasBeenViewed = userShopItem.hasBeenViewed,
                    onToggleBookmark = {
                        onShopItemCheckedChanged(
                            userShopItem.id,
                            !userShopItem.isSaved,
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}

fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor).build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()

    customTabsIntent.launchUrl(context, uri)
}

/**
 * A sealed hierarchy describing the state of the feed of shop items.
 */
sealed interface ItemFeedUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : ItemFeedUiState

    /**
     * The feed is loaded with the given list of shop items.
     */
    data class Success(
        /**
         * The list of shop items contained in this feed.
         */
        val feed: List<UserShopItem>,
    ) : ItemFeedUiState
}

@Preview
@Composable
private fun ItemFeedLoadingPreview() {
    AppTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(150.dp)) {
            itemFeed(
                feedState = ItemFeedUiState.Loading,
                onShopItemCheckedChanged = { _, _ -> },
                onShopItemViewed = {},
                onProductClick = {},
            )
        }
    }
}

@Preview
@Preview(device = Devices.TABLET)
@Composable
private fun ItemFeedContentPreview(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopResources: List<UserShopItem>,
) {
    AppThemeWithAnimationScopes {
        LazyVerticalStaggeredGrid(
            columns = Adaptive(150.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalItemSpacing = 16.dp,
        ) {
            itemFeed(
                feedState = ItemFeedUiState.Success(userShopResources),
                onShopItemCheckedChanged = { _, _ -> },
                onShopItemViewed = {},
                onProductClick = {},
            )
        }
    }
}
