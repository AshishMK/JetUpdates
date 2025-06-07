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

package com.demo.jetupdates.core.testing.repository

import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.model.data.DarkThemeConfig
import com.demo.jetupdates.core.model.data.ThemeBrand
import com.demo.jetupdates.core.model.data.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

val emptyUserData = UserData(
    bookmarkedShopItems = emptySet(),
    viewedShopItems = emptySet(),
    followedCategories = emptySet(),
    themeBrand = ThemeBrand.DEFAULT,
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    useDynamicColor = false,
    shouldHideOnboarding = false,
)

class TestUserDataRepository : UserDataRepository {
    /**
     * The backing hot flow for the list of followed category ids for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()

    override suspend fun setFollowedCategoryIds(followedCategoryIds: Set<Int>) {
        _userData.tryEmit(currentUserData.copy(followedCategories = followedCategoryIds))
    }

    override suspend fun setCategoryIdFollowed(followedCategoryId: Int, followed: Boolean) {
        currentUserData.let { current ->
            val followedCategories = if (followed) {
                current.followedCategories + followedCategoryId
            } else {
                current.followedCategories - followedCategoryId
            }

            _userData.tryEmit(current.copy(followedCategories = followedCategories))
        }
    }

    override suspend fun setShopItemBookmarked(shopItemId: Int, bookmarked: Boolean) {
        currentUserData.let { current ->
            val bookmarkedItems = if (bookmarked) {
                current.bookmarkedShopItems + shopItemId
            } else {
                current.bookmarkedShopItems - shopItemId
            }

            _userData.tryEmit(current.copy(bookmarkedShopItems = bookmarkedItems))
        }
    }

    override suspend fun setShopItemViewed(shopItemId: Int, viewed: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    viewedShopItems =
                    if (viewed) {
                        current.viewedShopItems + shopItemId
                    } else {
                        current.viewedShopItems - shopItemId
                    },
                ),
            )
        }
    }

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(themeBrand = themeBrand))
        }
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(darkThemeConfig = darkThemeConfig))
        }
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(useDynamicColor = useDynamicColor))
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(shouldHideOnboarding = shouldHideOnboarding))
        }
    }

    /**
     * A test-only API to allow setting of user data directly.
     */
    fun setUserData(userData: UserData) {
        _userData.tryEmit(userData)
    }
}
