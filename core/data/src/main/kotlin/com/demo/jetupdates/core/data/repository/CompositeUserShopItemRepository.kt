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

package com.demo.jetupdates.core.data.repository

import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.model.data.mapToUserShopItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements a [UserShopItemRepository] by combining a [ShopRepository] with a
 * [UserDataRepository].
 */
class CompositeUserShopItemRepository @Inject constructor(
    val shopRepository: ShopRepository,
    val userDataRepository: UserDataRepository,
) : UserShopItemRepository {

    /**
     * Returns available shop items (joined with user data) matching the given query.
     */
    override fun observeAll(
        query: ShopItemQuery,
    ): Flow<List<UserShopItem>> =
        shopRepository.getShopItems(query)
            .combine(userDataRepository.userData) { shopItems, userData ->
                shopItems.mapToUserShopItems(userData)
            }

    /**
     * Returns available shop items (joined with user data) for the followed categories.
     */
    override fun observeAllForFollowedCategories(): Flow<List<UserShopItem>> =
        userDataRepository.userData.map { it.followedCategories }.distinctUntilChanged()
            .flatMapLatest { followedCategories ->
                when {
                    followedCategories.isEmpty() -> flowOf(emptyList())
                    else -> observeAll(ShopItemQuery(filterCategoryIds = followedCategories))
                }
            }

    override fun observeAllBookmarked(): Flow<List<UserShopItem>> =
        userDataRepository.userData.map { it.bookmarkedShopItems }.distinctUntilChanged()
            .flatMapLatest { bookmarkedShopItems ->
                when {
                    bookmarkedShopItems.isEmpty() -> flowOf(emptyList())
                    else -> observeAll(ShopItemQuery(filterItemIds = bookmarkedShopItems))
                }
            }
}
