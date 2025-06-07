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

package com.demo.jetupdates.core.domain

import com.demo.jetupdates.core.data.repository.SearchContentsRepository
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.model.data.SearchResult
import com.demo.jetupdates.core.model.data.UserData
import com.demo.jetupdates.core.model.data.UserSearchResult
import com.demo.jetupdates.core.model.data.UserShopItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which returns the searched contents matched with the search query.
 */
class GetSearchContentsUseCase @Inject constructor(
    private val searchContentsRepository: SearchContentsRepository,
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(
        searchQuery: String,
    ): Flow<UserSearchResult> =
        searchContentsRepository.searchContents(searchQuery)
            .mapToUserSearchResult(userDataRepository.userData)
}

private fun Flow<SearchResult>.mapToUserSearchResult(userDataStream: Flow<UserData>): Flow<UserSearchResult> =
    combine(userDataStream) { searchResult, userData ->
        UserSearchResult(
            categories = searchResult.categories.map { category ->
                FollowableCategory2(
                    category = category,
                    isFollowed = category.id in userData.followedCategories,
                )
            },
            shopItems = searchResult.shopItems.map { shopItem ->
                UserShopItem(
                    shopItem = shopItem,
                    userData = userData,
                )
            },
        )
    }
