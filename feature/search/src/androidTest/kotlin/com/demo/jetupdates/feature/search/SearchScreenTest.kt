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

package com.demo.jetupdates.feature.search

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import com.demo.jetupdates.core.data.model.RecentSearchQuery
import com.demo.jetupdates.core.model.data.DarkThemeConfig.DARK
import com.demo.jetupdates.core.model.data.ThemeBrand.ANDROID
import com.demo.jetupdates.core.model.data.UserData
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.testing.data.followableCategoryTestData
import com.demo.jetupdates.core.testing.data.shopItemsTestData
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.core.ui.LocalSharedTransitionScope
import com.demo.jetupdates.core.ui.R.string
import com.demo.jetupdates.feature.search.SearchResultUiState.EmptyQuery
import com.demo.jetupdates.feature.search.SearchResultUiState.SearchNotReady
import com.demo.jetupdates.feature.search.SearchResultUiState.Success
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI test for checking the correct behaviour of the Search screen.
 */
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var clearSearchContentDesc: String
    private lateinit var followButtonContentDesc: String
    private lateinit var unfollowButtonContentDesc: String
    private lateinit var clearRecentSearchesContentDesc: String
    private lateinit var categoriesString: String
    private lateinit var updatesString: String
    private lateinit var tryAnotherSearchString: String
    private lateinit var searchNotReadyString: String

    private val userData: UserData = UserData(
        bookmarkedShopItems = setOf(1, 3),
        viewedShopItems = setOf(1, 2, 4),
        followedCategories = emptySet(),
        themeBrand = ANDROID,
        darkThemeConfig = DARK,
        shouldHideOnboarding = true,
        useDynamicColor = false,
    )

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            clearSearchContentDesc =
                getString(R.string.feature_search_clear_search_text_content_desc)
            clearRecentSearchesContentDesc =
                getString(R.string.feature_search_clear_recent_searches_content_desc)
            followButtonContentDesc =
                getString(string.core_ui_trending_card_follow_button_content_desc)
            unfollowButtonContentDesc =
                getString(string.core_ui_trending_card_unfollow_button_content_desc)
            categoriesString = getString(R.string.feature_search_categories)
            updatesString = getString(R.string.feature_search_updates)
            tryAnotherSearchString = getString(R.string.feature_search_try_another_search) +
                " " + getString(R.string.feature_search_trending) + " " + getString(R.string.feature_search_to_browse_categories)
            searchNotReadyString = getString(R.string.feature_search_not_ready)
        }
    }

    @Test
    fun searchTextField_isFocused() {
        appThemeWithAnimationScopes {
            SearchScreen()
        }

        composeTestRule
            .onNodeWithTag("searchTextField")
            .assertIsFocused()
    }

    @Test
    fun emptySearchResult_emptyScreenIsDisplayed() {
        appThemeWithAnimationScopes {
            SearchScreen(
                searchResultUiState = Success(),
            )
        }

        composeTestRule
            .onNodeWithText(tryAnotherSearchString)
            .assertIsDisplayed()
    }

    @Test
    fun emptySearchResult_nonEmptyRecentSearches_emptySearchScreenAndRecentSearchesAreDisplayed() {
        val recentSearches = listOf("kotlin")
        appThemeWithAnimationScopes {
            SearchScreen(
                searchResultUiState = Success(),
                recentSearchesUiState = RecentSearchQueriesUiState.Success(
                    recentQueries = recentSearches.map(::RecentSearchQuery),
                ),
            )
        }

        composeTestRule
            .onNodeWithText(tryAnotherSearchString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription(clearRecentSearchesContentDesc)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("kotlin")
            .assertIsDisplayed()
    }

    @Test
    fun searchResultWithTopics_allTopicsAreVisible_followButtonsVisibleForTheNumOfFollowedTopics() {
        appThemeWithAnimationScopes {
            SearchScreen(
                searchResultUiState = Success(categories = followableCategoryTestData),
            )
        }

        composeTestRule
            .onNodeWithText(categoriesString)
            .assertIsDisplayed()

        val scrollableNode = composeTestRule
            .onAllNodes(hasScrollToNodeAction())
            .onFirst()

        followableCategoryTestData.forEachIndexed { index, followableCategory ->
            scrollableNode.performScrollToIndex(index)

            composeTestRule
                .onNodeWithText(followableCategory.category.name)
                .assertIsDisplayed()
        }

        composeTestRule
            .onAllNodesWithContentDescription(followButtonContentDesc)
            .assertCountEquals(2)
        composeTestRule
            .onAllNodesWithContentDescription(unfollowButtonContentDesc)
            .assertCountEquals(1)
    }

    @Test
    fun searchResultWithNewsResources_firstNewsResourcesIsVisible() {
        appThemeWithAnimationScopes {
            SearchScreen(
                searchResultUiState = Success(
                    shopItems = shopItemsTestData.map {
                        UserShopItem(
                            shopItem = it,
                            userData = userData,
                        )
                    },
                ),
            )
        }

        composeTestRule
            .onNodeWithText(updatesString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(shopItemsTestData[0].title)
            .assertIsDisplayed()
    }

    @Test
    fun emptyQuery_notEmptyRecentSearches_verifyClearSearchesButton_displayed() {
        val recentSearches = listOf("kotlin", "testing")
        appThemeWithAnimationScopes {
            SearchScreen(
                searchResultUiState = EmptyQuery,
                recentSearchesUiState = RecentSearchQueriesUiState.Success(
                    recentQueries = recentSearches.map(::RecentSearchQuery),
                ),
            )
        }

        composeTestRule
            .onNodeWithContentDescription(clearRecentSearchesContentDesc)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("kotlin")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("testing")
            .assertIsDisplayed()
    }

    @Test
    fun searchNotReady_verifySearchNotReadyMessageIsVisible() {
        appThemeWithAnimationScopes {
            SearchScreen(
                searchResultUiState = SearchNotReady,
            )
        }

        composeTestRule
            .onNodeWithText(searchNotReadyString)
            .assertIsDisplayed()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    fun appThemeWithAnimationScopes(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
