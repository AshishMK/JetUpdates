/*
 * Copyright 2024 The Android Open Source Project
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

package com.demo.jetupdates.ui.trending2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.demo.jetupdates.feature.trending.navigation.TrendingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

const val CATEGORY_ID_KEY = "selectedCategoryId"

@HiltViewModel
class Trending2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val route = savedStateHandle.toRoute<TrendingRoute>()
    val selectedTopicId: StateFlow<Int?> = savedStateHandle.getStateFlow(
        key = CATEGORY_ID_KEY,
        initialValue = route.initialCategoryId,
    )

    fun onTopicClick(topicId: Int?) {
        savedStateHandle[CATEGORY_ID_KEY] = topicId
    }
}
