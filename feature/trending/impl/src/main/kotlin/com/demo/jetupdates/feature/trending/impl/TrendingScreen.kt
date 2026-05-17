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

package com.demo.jetupdates.feature.trending.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.component.AppLoadingWheel
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.ui.DevicePreviews
import com.demo.jetupdates.core.ui.FollowableCategoryPreviewParameterProvider
import com.demo.jetupdates.feature.trending.api.R
import com.demo.jetupdates.feature.trending.impl.TrendingUiState.Empty
import com.demo.jetupdates.feature.trending.impl.TrendingUiState.Loading
import com.demo.jetupdates.feature.trending.impl.TrendingUiState.Trending

@Composable
fun TrendingScreen(
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shouldHighlightSelectedCategory: Boolean = false,
    viewModel: TrendingViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrendingScreen(
        uiState = uiState,
        followCategory = viewModel::followCategory,
        onCategoryClick = {
            // TODO: this violates SSOT, events should go through the ViewModel see onCategoryClick in viewmodel
            viewModel.onCategoryClick(it)
            onCategoryClick(it)
        },
        shouldHighlightSelectedCategory = shouldHighlightSelectedCategory,
        modifier = modifier,
    )
}

@Composable
internal fun TrendingScreen(
    uiState: TrendingUiState,
    followCategory: (Int, Boolean) -> Unit,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shouldHighlightSelectedCategory: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            Loading ->
                AppLoadingWheel(
                    contentDesc = stringResource(id = R.string.feature_trending_api_loading),
                )

            is Trending ->
                CategoriesTabContent(
                    categories = uiState.categories,
                    onCategoryClick = onCategoryClick,
                    onFollowButtonClick = followCategory,
                    selectedCategoryId = uiState.selectedCategoryId,
                    shouldHighlightSelectedCategory = shouldHighlightSelectedCategory,
                )

            is Empty -> InterestsEmptyScreen()
        }
    }
    //  TrackScreenViewEvent(screenName = "Interests")
}

@Composable
private fun InterestsEmptyScreen() {
    Text(text = stringResource(id = R.string.feature_trending_api_empty_header))
}

@DevicePreviews
@Composable
fun InterestsScreenPopulated(
    @PreviewParameter(FollowableCategoryPreviewParameterProvider::class)
    followableTopics: List<FollowableCategory2>,
) {
    AppTheme {
        AppBackground {
            TrendingScreen(
                uiState = TrendingUiState.Trending(
                    selectedCategoryId = null,
                    categories = followableTopics,
                ),
                followCategory = { _, _ -> },
                onCategoryClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenLoading() {
    AppTheme {
        AppBackground {
            TrendingScreen(
                uiState = TrendingUiState.Loading,
                followCategory = { _, _ -> },
                onCategoryClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenEmpty() {
    AppTheme {
        AppBackground {
            TrendingScreen(
                uiState = TrendingUiState.Empty,
                followCategory = { _, _ -> },
                onCategoryClick = {},
            )
        }
    }
}
