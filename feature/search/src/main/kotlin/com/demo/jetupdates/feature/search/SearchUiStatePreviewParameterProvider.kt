/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("ktlint:standard:max-line-length")

package com.demo.jetupdates.feature.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.ui.PreviewParameterData.categories
import com.demo.jetupdates.core.ui.PreviewParameterData.shopItems
import com.demo.jetupdates.feature.search.SearchResultUiState

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [SearchResultUiState] for Composable previews.
 */
class SearchUiStatePreviewParameterProvider : PreviewParameterProvider<SearchResultUiState> {
    override val values: Sequence<SearchResultUiState> = sequenceOf(
        SearchResultUiState.Success(
            categories = categories.mapIndexed { i, category ->
                FollowableCategory2(category = category, isFollowed = i % 2 == 0)
            },
            shopItems = shopItems,
        ),
    )
}
