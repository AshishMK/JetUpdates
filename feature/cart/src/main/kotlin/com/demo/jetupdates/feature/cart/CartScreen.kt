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

package com.demo.jetupdates.feature.cart

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.jetupdates.core.designsystem.component.AppLoadingWheel
import com.demo.jetupdates.core.designsystem.component.scrollbar.DraggableScrollbar
import com.demo.jetupdates.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.demo.jetupdates.core.designsystem.component.scrollbar.scrollbarState
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.designsystem.theme.LocalTintTheme
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.ui.ItemFeedUiState
import com.demo.jetupdates.core.ui.TrackScrollJank
import com.demo.jetupdates.core.ui.UserShopResourcePreviewParameterProvider
import com.demo.jetupdates.core.ui.itemFeed

@Composable
internal fun CartRoute(
    onTopicClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val feedState by viewModel.feedUiState.collectAsStateWithLifecycle()
    CartScreen(
        feedState = feedState,
        onShowSnackbar = onShowSnackbar,
        removeFromCart = viewModel::removeFromSavedResources,
        onShopItemViewed = { viewModel.setShopItemViewed(it, true) },
        onTopicClick = onTopicClick,
        modifier = modifier,
        shouldDisplayUndoItem = viewModel.shouldDisplayUndoItem,
        undoItemRemoval = viewModel::undoItemRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

/**
 * Displays the user's bookmarked articles. Includes support for loading and empty states.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Composable
internal fun CartScreen(
    feedState: ItemFeedUiState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    removeFromCart: (Int) -> Unit,
    onShopItemViewed: (Int) -> Unit,
    onTopicClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shouldDisplayUndoItem: Boolean = false,
    undoItemRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    val bookmarkRemovedMessage = stringResource(id = R.string.feature_cart_removed)
    val undoText = stringResource(id = R.string.feature_cart_undo)

    LaunchedEffect(shouldDisplayUndoItem) {
        if (shouldDisplayUndoItem) {
            val snackBarResult = onShowSnackbar(bookmarkRemovedMessage, undoText)
            if (snackBarResult) {
                undoItemRemoval()
            } else {
                clearUndoState()
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        clearUndoState()
    }

    when (feedState) {
        ItemFeedUiState.Loading -> LoadingState(modifier)
        is ItemFeedUiState.Success -> if (feedState.feed.isNotEmpty()) {
            BookmarksGrid(
                feedState,
                removeFromCart,
                onShopItemViewed,
                onTopicClick,
                modifier,
            )
        } else {
            EmptyState(modifier)
        }
    }

    // TrackScreenViewEvent(screenName = "Saved")
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    AppLoadingWheel(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
            .testTag("cart:loading"),
        contentDesc = stringResource(id = R.string.feature_cart_loading),
    )
}

@Composable
private fun BookmarksGrid(
    feedState: ItemFeedUiState,
    removeFromCart: (Int) -> Unit,
    onShopItemViewed: (Int) -> Unit,
    onTopicClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollableState = rememberLazyStaggeredGridState()
    TrackScrollJank(scrollableState = scrollableState, stateName = "bookmarks:grid")
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalItemSpacing = 16.dp,
            state = scrollableState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("cart:feed"),
        ) {
            itemFeed(
                feedState = feedState,
                onShopItemCheckedChanged = { id, _ -> removeFromCart(id) },
                onShopItemViewed = onShopItemViewed,
                onCategoryClick = onTopicClick,
            )
            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
        val itemsAvailable = when (feedState) {
            ItemFeedUiState.Loading -> 1
            is ItemFeedUiState.Success -> feedState.feed.size
        }
        val scrollbarState = scrollableState.scrollbarState(
            itemsAvailable = itemsAvailable,
        )
        scrollableState.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = scrollableState.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .testTag("cart:empty"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val iconTint = LocalTintTheme.current.iconTint
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.feature_cart_img_empty_cart),
            colorFilter = if (iconTint != Color.Unspecified) ColorFilter.tint(iconTint) else null,
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(id = R.string.feature_cart_empty_error),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.feature_cart_empty_description),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun LoadingStatePreview() {
    AppTheme {
        LoadingState()
    }
}

@Preview
@Composable
private fun BookmarksGridPreview(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userNewsResources: List<UserShopItem>,
) {
    AppTheme {
        BookmarksGrid(
            feedState = ItemFeedUiState.Success(userNewsResources),
            removeFromCart = {},
            onShopItemViewed = {},
            onTopicClick = {},
        )
    }
}

@Preview
@Composable
private fun EmptyStatePreview() {
    AppTheme {
        EmptyState()
    }
}
