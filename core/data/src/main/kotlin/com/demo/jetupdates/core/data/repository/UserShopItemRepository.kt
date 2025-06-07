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
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [UserShopItem]
 */
interface UserShopItemRepository {
    /**
     * Returns available shop items as a stream.
     */
    fun observeAll(
        query: ShopItemQuery = ShopItemQuery(
            filterCategoryIds = null,
            filterItemIds = null,
        ),
    ): Flow<List<UserShopItem>>

    /**
     * Returns available Shop items for the user's followed Shop  Categories as a stream.
     */
    fun observeAllForFollowedCategories(): Flow<List<UserShopItem>>

    /**
     * Returns the user's bookmarked shop items as a stream.
     */
    fun observeAllBookmarked(): Flow<List<UserShopItem>>
}
