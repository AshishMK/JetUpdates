/*
 * Copyright 2025 The Android Open Source Project
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

package com.demo.jetupdates.feature.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.jetupdates.core.data.repository.ShopItemQuery
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.data.repository.UserShopItemRepository
import com.demo.jetupdates.core.data.util.SyncManager
import com.demo.jetupdates.core.domain.GetFollowableCategoriesUseCase
import com.demo.jetupdates.core.notifications.DEEP_LINK_SHOP_ITEM_ID_KEY
import com.demo.jetupdates.core.ui.ItemFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
    private val userDataRepository: UserDataRepository,
    userShopItemRepository: UserShopItemRepository,
    getFollowableCategories: GetFollowableCategoriesUseCase,
) : ViewModel() {

    var useCategoryClicked = MutableStateFlow(false)
    var shouldShowOnboarding = true
    private val shouldShowOnboardingCombineCategoryAction: Flow<Boolean> =
        combine(
            userDataRepository.userData.map { !it.shouldHideOnboarding },
            useCategoryClicked,
        ) { (shouldShowOnboardingFromUserData, useCategoryClicked) ->
            shouldShowOnboarding = shouldShowOnboardingFromUserData
            shouldShowOnboardingFromUserData || useCategoryClicked
        }

    val deepLinkedShopItem = savedStateHandle.getStateFlow<Int?>(
        key = DEEP_LINK_SHOP_ITEM_ID_KEY,
        null,
    )
        .flatMapLatest { shopItemId ->
            if (shopItemId == null) {
                flowOf(emptyList())
            } else {
                userShopItemRepository.observeAll(
                    ShopItemQuery(
                        filterItemIds = setOf(shopItemId),
                    ),
                )
            }
        }
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val feedState: StateFlow<ItemFeedUiState> =
        userShopItemRepository.observeAllForFollowedCategories()
            .map(ItemFeedUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ItemFeedUiState.Loading,
            )

    val onboardingUiState: StateFlow<OnboardingUiState> =
        combine(
            shouldShowOnboardingCombineCategoryAction,
            getFollowableCategories(),
        ) { shouldShowOnboardingCombineCategoryAction, categories ->
            if (shouldShowOnboardingCombineCategoryAction) {
                OnboardingUiState.Shown(categories = categories, shouldShowOnboarding = shouldShowOnboarding)
            } else {
                OnboardingUiState.NotShown
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = OnboardingUiState.Loading,
            )

    fun updateCategorySelection(categoryId: Int, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setCategoryIdFollowed(categoryId, isChecked)
        }
    }

    fun updateShopItemSaved(shopItemId: Int, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShopItemBookmarked(shopItemId, isChecked)
        }
    }

    fun setShopItemViewed(shopItemId: Int, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShopItemViewed(shopItemId, viewed)
        }
    }

    fun onDeepLinkOpened(shopItemId: Int) {
        if (shopItemId == deepLinkedShopItem.value?.id) {
            savedStateHandle[DEEP_LINK_SHOP_ITEM_ID_KEY] = null
        }
        viewModelScope.launch {
            userDataRepository.setShopItemViewed(
                shopItemId = shopItemId,
                viewed = true,
            )
        }
    }

    fun dismissOnboarding() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnboarding(true)
        }
    }

    fun categoryActionClicked(show: Boolean) {
        useCategoryClicked.value = show
    }
}
