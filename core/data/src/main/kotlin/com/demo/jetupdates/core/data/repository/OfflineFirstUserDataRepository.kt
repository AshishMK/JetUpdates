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

package com.demo.jetupdates.core.data.repository

import androidx.annotation.VisibleForTesting
import com.demo.jetupdates.core.datastore.AppPreferencesDataSource
import com.demo.jetupdates.core.model.data.DarkThemeConfig
import com.demo.jetupdates.core.model.data.ThemeBrand
import com.demo.jetupdates.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        appPreferencesDataSource.userData

    @VisibleForTesting
    override suspend fun setFollowedCategoryIds(followedCategoryIds: Set<Int>) =
        appPreferencesDataSource.setFollowedCategoryIds(followedCategoryIds)

    override suspend fun setCategoryIdFollowed(followedCategoryId: Int, followed: Boolean) {
        appPreferencesDataSource.setCategoryIdFollowed(followedCategoryId, followed)
    }

    override suspend fun setShopItemBookmarked(shopItemId: Int, bookmarked: Boolean) {
        appPreferencesDataSource.setShopItemBookmarked(shopItemId, bookmarked)
    }

    override suspend fun setShopItemViewed(shopItemId: Int, viewed: Boolean) =
        appPreferencesDataSource.setShopItemViewed(shopItemId, viewed)

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        appPreferencesDataSource.setThemeBrand(themeBrand)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        appPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        appPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        appPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }
}
