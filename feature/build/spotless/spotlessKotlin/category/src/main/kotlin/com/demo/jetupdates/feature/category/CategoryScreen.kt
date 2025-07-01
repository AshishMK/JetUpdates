/*
 * Copyright 2021 The Android Open Source Project
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

package com.demo.jetupdates.feature.category

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.component.AppFilterChip
import com.demo.jetupdates.core.designsystem.component.AppLoadingWheel
import com.demo.jetupdates.core.designsystem.component.DynamicAsyncImage
import com.demo.jetupdates.core.designsystem.component.scrollbar.DraggableScrollbar
import com.demo.jetupdates.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.demo.jetupdates.core.designsystem.component.scrollbar.scrollbarState
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.ui.CategoriesToDrawable.mapDrawables
import com.demo.jetupdates.core.ui.DevicePreviews
import com.demo.jetupdates.core.ui.TrackScrollJank
import com.demo.jetupdates.core.ui.UserShopResourcePreviewParameterProvider
import com.demo.jetupdates.core.ui.userShopItemCardItems
import com.demo.jetupdates.feature.category.CategoryUiState.Error
import com.demo.jetupdates.feature.category.CategoryUiState.Success
import com.demo.jetupdates.feature.category.R.string

@Composable
fun CategoryScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val categoryUiState: CategoryUiState by viewModel.categoryUiState.collectAsStateWithLifecycle()
    val shopItemUiState: ShopItemUiState by viewModel.shopItemUiState.collectAsStateWithLifecycle()

    // TrackScreenViewEvent(screenName = "Category: ${viewModel.categoryId}")
    CategoryScreen(
        categoryUiState = categoryUiState,
        shopItemUiState = shopItemUiState,
        modifier = modifier.testTag("category:${viewModel.categoryId}"),
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        onFollowClick = viewModel::followCategoryToggle,
        onBookmarkChanged = viewModel::bookmarkItem,
        onShopItemViewed = { viewModel.setShopItemViewed(it, true) },
        onCategoryClick = onCategoryClick,
    )
}

@VisibleForTesting
@Composable
internal fun CategoryScreen(
    categoryUiState: CategoryUiState,
    shopItemUiState: ShopItemUiState,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onFollowClick: (Boolean) -> Unit,
    onCategoryClick: (Int) -> Unit,
    onBookmarkChanged: (Int, Boolean) -> Unit,
    onShopItemViewed: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyStaggeredGridState()
    TrackScrollJank(scrollableState = state, stateName = "category:screen")
    Box(
        modifier = modifier,
    ) {
        LazyVerticalStaggeredGrid(

            columns = StaggeredGridCells.Adaptive(150.dp),

            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            state = state,
        ) {
            item(span = StaggeredGridItemSpan.FullLine, contentType = "topSpacing") {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            }
            when (categoryUiState) {
                CategoryUiState.Loading -> item {
                    AppLoadingWheel(
                        modifier = modifier,
                        contentDesc = stringResource(id = string.feature_category_loading),
                    )
                }

                Error -> TODO()
                is Success -> {
                    item(span = StaggeredGridItemSpan.FullLine, contentType = "categoryToolbar") {
                        CategoryToolbar(
                            showBackButton = showBackButton,
                            onBackClick = onBackClick,
                            onFollowClick = onFollowClick,
                            uiState = categoryUiState.followableCategory,
                        )
                    }
                    categoryBody(
                        id = categoryUiState.followableCategory.category.id,
                        name = categoryUiState.followableCategory.category.name,
                        description = categoryUiState.followableCategory.category.longDescription,
                        news = shopItemUiState,
                        imageUrl = categoryUiState.followableCategory.category.imageUrl,
                        onBookmarkChanged = onBookmarkChanged,
                        onNewsResourceViewed = onShopItemViewed,
                        onCategoryClick = onCategoryClick,
                    )
                }
            }
            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
        val itemsAvailable = shopItemsSize(categoryUiState, shopItemUiState)
        val scrollbarState = state.scrollbarState(
            itemsAvailable = itemsAvailable,
        )
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
}

private fun shopItemsSize(
    categoryUiState: CategoryUiState,
    shopItemUiState: ShopItemUiState,
) = when (categoryUiState) {
    Error -> 0 // Nothing
    CategoryUiState.Loading -> 1 // Loading bar
    is Success -> when (shopItemUiState) {
        ShopItemUiState.Error -> 0 // Nothing
        ShopItemUiState.Loading -> 1 // Loading bar
        is ShopItemUiState.Success -> 2 + shopItemUiState.items.size // Toolbar, header
    }
}

private fun LazyStaggeredGridScope.categoryBody(
    id: Int,
    name: String,
    description: String,
    news: ShopItemUiState,
    imageUrl: String,
    onBookmarkChanged: (Int, Boolean) -> Unit,
    onNewsResourceViewed: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit,
) {
    // TODO: Show icon if available
    item(span = StaggeredGridItemSpan.FullLine, contentType = "categoryHeader") {
        CategoryHeader(id, name, description, imageUrl)
    }

    userShopItemCards(news, onBookmarkChanged, onNewsResourceViewed, onCategoryClick)
}

@Composable
private fun CategoryHeader(id: Int, name: String, description: String, imageUrl: String) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        val placeHolder = mapDrawables[id]!!
        DynamicAsyncImage(
            imageUrl = "",
            placeholder = painterResource(placeHolder),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(132.dp)
                .padding(bottom = 12.dp),
        )
        Text(name, style = MaterialTheme.typography.displayMedium)
        if (description.isNotEmpty()) {
            Text(
                description,
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

// TODO: Could/should this be replaced with [LazyGridScope.newsFeed]?
private fun LazyStaggeredGridScope.userShopItemCards(
    shopState: ShopItemUiState,
    onBookmarkChanged: (Int, Boolean) -> Unit,
    onShopItemViewed: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit,
) {
    when (shopState) {
        is ShopItemUiState.Success -> {
            userShopItemCardItems(
                items = shopState.items,
                onToggleBookmark = { onBookmarkChanged(it.id, !it.isSaved) },
                onShopItemViewed = onShopItemViewed,
                onCategoryClick = onCategoryClick,
                itemModifier = Modifier.padding(0.dp),
            )
        }

        is ShopItemUiState.Loading -> item {
            AppLoadingWheel(contentDesc = "Loading news") // TODO
        }

        else -> item {
            Text("Error") // TODO
        }
    }
}

@Preview
@Composable
private fun CategoryBodyPreview() {
    AppTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(150.dp)) {
            categoryBody(
                id = 1,
                name = "Jetpack Compose",
                description = "Lorem ipsum maximum",
                news = ShopItemUiState.Success(emptyList()),
                imageUrl = "",
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onCategoryClick = {},
            )
        }
    }
}

@Composable
private fun CategoryToolbar(
    uiState: FollowableCategory2,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    onFollowClick: (Boolean) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
    ) {
        if (showBackButton) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = AppIcons.ArrowBack,
                    contentDescription = stringResource(
                        id = com.demo.jetupdates.core.ui.R.string.core_ui_back,
                    ),
                )
            }
        } else {
            // Keeps the NiaFilterChip aligned to the end of the Row.
            Spacer(modifier = Modifier.width(1.dp))
        }
        val selected = uiState.isFollowed
        AppFilterChip(
            selected = selected,
            onSelectedChange = onFollowClick,
            modifier = Modifier.padding(end = 0.dp),
        ) {
            if (selected) {
                Text("FOLLOWING")
            } else {
                Text("NOT FOLLOWING")
            }
        }
    }
}

@DevicePreviews
@Composable
fun CategoryScreenPopulated(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        AppBackground {
            CategoryScreen(
                categoryUiState = Success(userShopItems[0].followableCategories[0]),
                shopItemUiState = ShopItemUiState.Success(userShopItems),
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onBookmarkChanged = { _, _ -> },
                onShopItemViewed = {},
                onCategoryClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun CategoryScreenLoading() {
    AppTheme {
        AppBackground {
            CategoryScreen(
                categoryUiState = CategoryUiState.Loading,
                shopItemUiState = ShopItemUiState.Loading,
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onBookmarkChanged = { _, _ -> },
                onShopItemViewed = {},
                onCategoryClick = {},
            )
        }
    }
}
