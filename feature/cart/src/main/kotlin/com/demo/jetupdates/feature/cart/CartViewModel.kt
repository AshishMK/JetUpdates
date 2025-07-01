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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.data.repository.UserShopItemRepository
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.ui.ItemFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    userNewsResourceRepository: UserShopItemRepository,
) : ViewModel() {

    var shouldDisplayUndoItem by mutableStateOf(false)
    private var lastRemovedBookmarkId: Int? = null

    val feedUiState: StateFlow<ItemFeedUiState> =
        userNewsResourceRepository.observeAllBookmarked()
            .map<List<UserShopItem>, ItemFeedUiState>(ItemFeedUiState::Success)
            .onStart { emit(ItemFeedUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ItemFeedUiState.Loading,
            )

    fun removeFromSavedResources(newsResourceId: Int) {
        viewModelScope.launch {
            shouldDisplayUndoItem = true
            lastRemovedBookmarkId = newsResourceId
            userDataRepository.setShopItemBookmarked(newsResourceId, false)
        }
    }

    fun setShopItemViewed(newsResourceId: Int, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShopItemViewed(newsResourceId, viewed)
        }
    }

    fun undoItemRemoval() {
        viewModelScope.launch {
            lastRemovedBookmarkId?.let {
                userDataRepository.setShopItemBookmarked(it, true)
            }
        }
        clearUndoState()
    }

    fun clearUndoState() {
        shouldDisplayUndoItem = false
        lastRemovedBookmarkId = null
    }
}
