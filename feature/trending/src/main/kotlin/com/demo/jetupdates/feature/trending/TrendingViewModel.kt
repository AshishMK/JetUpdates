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

package com.demo.jetupdates.feature.trending

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.domain.CategorySortField
import com.demo.jetupdates.core.domain.GetFollowableCategoriesUseCase
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.feature.trending.TrendingUiState.Loading
import com.demo.jetupdates.feature.trending.navigation.TrendingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val userDataRepository: UserDataRepository,
    getFollowableCategories: GetFollowableCategoriesUseCase,
) : ViewModel() {

    // Key used to save and retrieve the currently selected topic id from saved state.
    private val selectedCategoryIdKey = "selectedCategoryIdKey"

    private val trendingRoute: TrendingRoute = savedStateHandle.toRoute()
    private val selectedTopicId = savedStateHandle.getStateFlow(
        key = selectedCategoryIdKey,
        initialValue = trendingRoute.initialCategoryId,
    )

    val uiState: StateFlow<TrendingUiState> = combine(
        selectedTopicId,
        getFollowableCategories(sortBy = CategorySortField.NAME),
        TrendingUiState::Trending,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Loading,
    )

    fun followCategory(followedCategoryId: Int, followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setCategoryIdFollowed(followedCategoryId, followed)
        }
    }

    fun onCategoryClick(categoryId: Int?) {
        savedStateHandle[selectedCategoryIdKey] = categoryId
    }
}

sealed interface TrendingUiState {
    data object Loading : TrendingUiState

    data class Trending(
        val selectedCategoryId: Int?,
        val categories: List<FollowableCategory2>,
    ) : TrendingUiState

    data object Empty : TrendingUiState
}
