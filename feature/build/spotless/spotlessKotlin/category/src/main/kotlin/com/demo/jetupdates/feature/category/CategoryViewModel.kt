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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.jetupdates.core.data.repository.CategoriesRepository
import com.demo.jetupdates.core.data.repository.ShopItemQuery
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.data.repository.UserShopItemRepository
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.result.Result
import com.demo.jetupdates.core.result.asResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CategoryViewModel.Factory::class)
class CategoryViewModel @AssistedInject constructor(
    private val userDataRepository: UserDataRepository,
    categoriesRepository: CategoriesRepository,
    userShopItemRepository: UserShopItemRepository,
    @Assisted val categoryId: Int,
) : ViewModel() {
    val categoryUiState: StateFlow<CategoryUiState> = categoryUiState(
        categoryId = categoryId,
        userDataRepository = userDataRepository,
        categoriesRepository = categoriesRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoryUiState.Loading,
        )

    val shopItemUiState: StateFlow<ShopItemUiState> = shopItemUiState(
        categoryId = categoryId,
        userDataRepository = userDataRepository,
        userNewsResourceRepository = userShopItemRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ShopItemUiState.Loading,
        )

    fun followCategoryToggle(followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setCategoryIdFollowed(categoryId, followed)
        }
    }

    fun bookmarkItem(shopItemId: Int, bookmarked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShopItemBookmarked(shopItemId, bookmarked)
        }
    }

    fun setShopItemViewed(shopItemId: Int, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShopItemViewed(shopItemId, viewed)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            categoryId: Int,
        ): CategoryViewModel
    }
}

private fun categoryUiState(
    categoryId: Int,
    userDataRepository: UserDataRepository,
    categoriesRepository: CategoriesRepository,
): Flow<CategoryUiState> {
    // Observe the followed categories, as they could change over time.
    val followedCategoryIds: Flow<Set<Int>> =
        userDataRepository.userData
            .map { it.followedCategories }

    // Observe category information
    val categoryStream: Flow<Category> = categoriesRepository.getCategory(
        id = categoryId,
    )

    return combine(
        followedCategoryIds,
        categoryStream,
        ::Pair,
    )
        .asResult()
        .map { followedCategoryToCategoryResult ->
            when (followedCategoryToCategoryResult) {
                is Result.Success -> {
                    val (followedCategories, category) = followedCategoryToCategoryResult.data
                    CategoryUiState.Success(
                        followableCategory = FollowableCategory2(
                            category = category,
                            isFollowed = categoryId in followedCategories,
                        ),
                    )
                }

                is Result.Loading -> CategoryUiState.Loading
                is Result.Error -> CategoryUiState.Error
            }
        }
}

private fun shopItemUiState(
    categoryId: Int,
    userNewsResourceRepository: UserShopItemRepository,
    userDataRepository: UserDataRepository,
): Flow<ShopItemUiState> {
    // Observe news
    val shopStream: Flow<List<UserShopItem>> = userNewsResourceRepository.observeAll(
        ShopItemQuery(filterCategoryIds = setOf(element = categoryId)),
    )

    // Observe bookmarks
    val bookmark: Flow<Set<Int>> = userDataRepository.userData
        .map { it.bookmarkedShopItems }

    return combine(shopStream, bookmark, ::Pair)
        .asResult()
        .map { shopToBookmarksResult ->
            when (shopToBookmarksResult) {
                is Result.Success -> ShopItemUiState.Success(shopToBookmarksResult.data.first)
                is Result.Loading -> ShopItemUiState.Loading
                is Result.Error -> ShopItemUiState.Error
            }
        }
}

sealed interface CategoryUiState {
    data class Success(val followableCategory: FollowableCategory2) : CategoryUiState
    data object Error : CategoryUiState
    data object Loading : CategoryUiState
}

sealed interface ShopItemUiState {
    data class Success(val items: List<UserShopItem>) : ShopItemUiState
    data object Error : ShopItemUiState
    data object Loading : ShopItemUiState
}
