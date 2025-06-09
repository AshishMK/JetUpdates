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

package com.demo.jetupdates.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.demo.jetupdates.core.datastore.DarkThemeConfigProto
import com.demo.jetupdates.core.datastore.ThemeBrandProto
import com.demo.jetupdates.core.model.data.DarkThemeConfig
import com.demo.jetupdates.core.model.data.ThemeBrand
import com.demo.jetupdates.core.model.data.UserData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AppPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                bookmarkedShopItems = it.bookmarkedShopItemIdsMap.keys,
                viewedShopItems = it.viewedShopItemIdsMap.keys,
                followedCategories = it.followedCategoryIdsMap.keys,
                themeBrand = when (it.themeBrand) {
                    null,
                    ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    ThemeBrandProto.UNRECOGNIZED,
                    ThemeBrandProto.THEME_BRAND_DEFAULT,
                    -> ThemeBrand.DEFAULT

                    ThemeBrandProto.THEME_BRAND_ANDROID -> ThemeBrand.ANDROID
                },
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    ->
                        DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        DarkThemeConfig.LIGHT

                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor,
                shouldHideOnboarding = it.shouldHideOnboarding,
            )
        }

    suspend fun setFollowedCategoryIds(categoryIds: Set<Int>) {
        try {
            userPreferences.updateData {
                it.copy {
                    followedCategoryIds.clear()
                    followedCategoryIds.putAll(categoryIds.associateWith { true })
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("AppPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setCategoryIdFollowed(categoryId: Int, followed: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (followed) {
                        followedCategoryIds.put(categoryId, true)
                    } else {
                        followedCategoryIds.remove(categoryId)
                    }
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("AppPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when (themeBrand) {
                    ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setShopItemBookmarked(shopItemId: Int, bookmarked: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (bookmarked) {
                        bookmarkedShopItemIds.put(shopItemId, true)
                    } else {
                        bookmarkedShopItemIds.remove(shopItemId)
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("AppPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setShopItemViewed(shopItemIds: Int, viewed: Boolean) {
        setShopItemsViewed(listOf(shopItemIds), viewed)
    }

    suspend fun setShopItemsViewed(shopItemIds: List<Int>, viewed: Boolean) {
        userPreferences.updateData { prefs ->
            prefs.copy {
                shopItemIds.forEach { id ->
                    if (viewed) {
                        viewedShopItemIds.put(id, true)
                    } else {
                        viewedShopItemIds.remove(id)
                    }
                }
            }
        }
    }

    suspend fun getChangeListVersions() = userPreferences.data
        .map {
            ChangeListVersions(
                categoryVersion = it.categoryChangeListVersion,
                shopItemVersion = it.shopItemChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    /**
     * Update the [ChangeListVersions] using [update].
     */
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.updateData { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        categoryVersion = currentPreferences.categoryChangeListVersion,
                        shopItemVersion = currentPreferences.shopItemChangeListVersion,
                    ),
                )

                currentPreferences.copy {
                    categoryChangeListVersion = updatedChangeListVersions.categoryVersion
                    shopItemChangeListVersion = updatedChangeListVersions.shopItemVersion
                }
            }
        } catch (ioException: IOException) {
            Log.e("AppPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedCategoryIds.isEmpty() && followedAuthorIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}
