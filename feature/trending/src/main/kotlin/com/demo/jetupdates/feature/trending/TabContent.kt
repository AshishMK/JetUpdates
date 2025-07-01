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

package com.demo.jetupdates.feature.trending

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.demo.jetupdates.core.designsystem.component.scrollbar.DraggableScrollbar
import com.demo.jetupdates.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.demo.jetupdates.core.designsystem.component.scrollbar.scrollbarState
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.ui.TrendingItem

@Composable
fun CategoriesTabContent(
    categories: List<FollowableCategory2>,
    onCategoryClick: (Int) -> Unit,
    onFollowButtonClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    withBottomSpacer: Boolean = true,
    selectedCategoryId: Int? = null,
    shouldHighlightSelectedCategory: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        val scrollableState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .testTag("trending:categories"),
            contentPadding = PaddingValues(vertical = 16.dp),
            state = scrollableState,
        ) {
            categories.forEach { followableCategory ->
                val categoryId = followableCategory.category.id
                item(key = categoryId) {
                    val isSelected =
                        shouldHighlightSelectedCategory && categoryId == selectedCategoryId
                    TrendingItem(
                        id = categoryId,
                        name = followableCategory.category.name,
                        following = followableCategory.isFollowed,
                        description = followableCategory.category.shortDescription,
                        categoryImageUrl = followableCategory.category.imageUrl,
                        onClick = { onCategoryClick(categoryId) },
                        onFollowButtonClick = { onFollowButtonClick(categoryId, it) },
                        isSelected = isSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            if (withBottomSpacer) {
                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
        val scrollbarState = scrollableState.scrollbarState(
            itemsAvailable = categories.size,
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
                itemsAvailable = categories.size,
            ),
        )
    }
}
