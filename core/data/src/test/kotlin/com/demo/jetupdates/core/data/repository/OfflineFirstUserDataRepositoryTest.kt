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

import com.demo.jetupdates.core.datastore.AppPreferencesDataSource
import com.demo.jetupdates.core.datastore.UserPreferences
import com.demo.jetupdates.core.datastore.test.InMemoryDataStore
import com.demo.jetupdates.core.model.data.DarkThemeConfig
import com.demo.jetupdates.core.model.data.ThemeBrand
import com.demo.jetupdates.core.model.data.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OfflineFirstUserDataRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstUserDataRepository

    private lateinit var appPreferencesDataSource: AppPreferencesDataSource

    @Before
    fun setup() {
        appPreferencesDataSource = AppPreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))

        subject = OfflineFirstUserDataRepository(
            appPreferencesDataSource = appPreferencesDataSource,
        )
    }

    @Test
    fun offlineFirstUserDataRepository_default_user_data_is_correct() =
        testScope.runTest {
            assertEquals(
                UserData(
                    bookmarkedShopItems = emptySet(),
                    viewedShopItems = emptySet(),
                    followedCategories = emptySet(),
                    themeBrand = ThemeBrand.DEFAULT,
                    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                    useDynamicColor = false,
                    shouldHideOnboarding = false,
                ),
                subject.userData.first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_toggle_followed_categories_logic_delegates_to_app_preferences() =
        testScope.runTest {
            subject.setCategoryIdFollowed(followedCategoryId = 0, followed = true)

            assertEquals(
                setOf(0),
                subject.userData
                    .map { it.followedCategories }
                    .first(),
            )

            subject.setCategoryIdFollowed(followedCategoryId = 1, followed = true)

            assertEquals(
                setOf(0, 1),
                subject.userData
                    .map { it.followedCategories }
                    .first(),
            )

            assertEquals(
                appPreferencesDataSource.userData
                    .map { it.followedCategories }
                    .first(),
                subject.userData
                    .map { it.followedCategories }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_followed_categories_logic_delegates_to_app_preferences() =
        testScope.runTest {
            subject.setFollowedCategoryIds(followedCategoryIds = setOf(1, 2))

            assertEquals(
                setOf(1, 2),
                subject.userData
                    .map { it.followedCategories }
                    .first(),
            )

            assertEquals(
                appPreferencesDataSource.userData
                    .map { it.followedCategories }
                    .first(),
                subject.userData
                    .map { it.followedCategories }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_bookmark_shop_item_logic_delegates_to_app_preferences() =
        testScope.runTest {
            subject.setShopItemBookmarked(shopItemId = 0, bookmarked = true)

            assertEquals(
                setOf(0),
                subject.userData
                    .map { it.bookmarkedShopItems }
                    .first(),
            )

            subject.setShopItemBookmarked(shopItemId = 1, bookmarked = true)

            assertEquals(
                setOf(0, 1),
                subject.userData
                    .map { it.bookmarkedShopItems }
                    .first(),
            )

            assertEquals(
                appPreferencesDataSource.userData
                    .map { it.bookmarkedShopItems }
                    .first(),
                subject.userData
                    .map { it.bookmarkedShopItems }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_update_viewed_shop_items_delegates_to_app_preferences() =
        runTest {
            subject.setShopItemViewed(shopItemId = 0, viewed = true)

            assertEquals(
                setOf(0),
                subject.userData
                    .map { it.viewedShopItems }
                    .first(),
            )

            subject.setShopItemViewed(shopItemId = 1, viewed = true)

            assertEquals(
                setOf(0, 1),
                subject.userData
                    .map { it.viewedShopItems }
                    .first(),
            )

            assertEquals(
                appPreferencesDataSource.userData
                    .map { it.viewedShopItems }
                    .first(),
                subject.userData
                    .map { it.viewedShopItems }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_theme_brand_delegates_to_app_preferences() =
        testScope.runTest {
            subject.setThemeBrand(ThemeBrand.ANDROID)

            assertEquals(
                ThemeBrand.ANDROID,
                subject.userData
                    .map { it.themeBrand }
                    .first(),
            )
            assertEquals(
                ThemeBrand.ANDROID,
                appPreferencesDataSource
                    .userData
                    .map { it.themeBrand }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_dynamic_color_delegates_to_app_preferences() =
        testScope.runTest {
            subject.setDynamicColorPreference(true)

            assertEquals(
                true,
                subject.userData
                    .map { it.useDynamicColor }
                    .first(),
            )
            assertEquals(
                true,
                appPreferencesDataSource
                    .userData
                    .map { it.useDynamicColor }
                    .first(),
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_dark_theme_config_delegates_to_app_preferences() =
        testScope.runTest {
            subject.setDarkThemeConfig(DarkThemeConfig.DARK)

            assertEquals(
                DarkThemeConfig.DARK,
                subject.userData
                    .map { it.darkThemeConfig }
                    .first(),
            )
            assertEquals(
                DarkThemeConfig.DARK,
                appPreferencesDataSource
                    .userData
                    .map { it.darkThemeConfig }
                    .first(),
            )
        }

    @Test
    fun whenUserCompletesOnboarding_thenRemovesAllInterests_shouldHideOnboardingIsFalse() =
        testScope.runTest {
            subject.setFollowedCategoryIds(setOf(1))
            subject.setShouldHideOnboarding(true)
            assertTrue(subject.userData.first().shouldHideOnboarding)

            subject.setFollowedCategoryIds(emptySet())
            assertFalse(subject.userData.first().shouldHideOnboarding)
        }
}
