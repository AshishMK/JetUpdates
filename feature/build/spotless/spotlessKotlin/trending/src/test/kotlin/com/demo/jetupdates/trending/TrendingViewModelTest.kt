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

package com.demo.jetupdates.trending

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.demo.jetupdates.core.domain.GetFollowableCategoriesUseCase
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.testing.repository.TestCategoriesRepository
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.util.MainDispatcherRule
import com.demo.jetupdates.feature.trending.TrendingUiState
import com.demo.jetupdates.feature.trending.TrendingViewModel
import com.demo.jetupdates.feature.trending.navigation.TrendingRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 *
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  See https://issuetracker.google.com/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class TrendingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val categoriesRepository = TestCategoriesRepository()
    private val getFollowableCategoriesUseCase = GetFollowableCategoriesUseCase(
        categoriesRepository = categoriesRepository,
        userDataRepository = userDataRepository,
    )
    private lateinit var viewModel: TrendingViewModel

    @Before
    fun setup() {
        viewModel = TrendingViewModel(
            savedStateHandle = SavedStateHandle(
                route = TrendingRoute(initialCategoryId = testInputCategories[0].category.id),
            ),
            userDataRepository = userDataRepository,
            getFollowableCategories = getFollowableCategoriesUseCase,
        )
    }

    @Test
    fun uiState_whenInitialized_thenShowLoading() = runTest {
        assertEquals(TrendingUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_whenFollowedCategoriesAreLoading_thenShowLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        userDataRepository.setFollowedCategoryIds(emptySet())
        assertEquals(TrendingUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_whenFollowingNewCategory_thenShowUpdatedCategories() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val toggleCategoryId = testOutputCategories[1].category.id
        categoriesRepository.sendCategories(testInputCategories.map { it.category })
        userDataRepository.setFollowedCategoryIds(setOf(testInputCategories[0].category.id))

        assertEquals(
            false,
            (viewModel.uiState.value as TrendingUiState.Trending)
                .categories.first { it.category.id == toggleCategoryId }.isFollowed,
        )

        viewModel.followCategory(
            followedCategoryId = toggleCategoryId,
            true,
        )

        assertEquals(
            TrendingUiState.Trending(
                categories = testOutputCategories,
                selectedCategoryId = testInputCategories[0].category.id,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun uiState_whenUnfollowingCategories_thenShowUpdatedCategories() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val toggleCategoryId = testOutputCategories[1].category.id

        categoriesRepository.sendCategories(testOutputCategories.map { it.category })
        userDataRepository.setFollowedCategoryIds(
            setOf(testOutputCategories[0].category.id, testOutputCategories[1].category.id),
        )

        assertEquals(
            true,
            (viewModel.uiState.value as TrendingUiState.Trending)
                .categories.first { it.category.id == toggleCategoryId }.isFollowed,
        )

        viewModel.followCategory(
            followedCategoryId = toggleCategoryId,
            false,
        )

        assertEquals(
            TrendingUiState.Trending(
                categories = testInputCategories,
                selectedCategoryId = testInputCategories[0].category.id,
            ),
            viewModel.uiState.value,
        )
    }
}

private const val CATEGORY_1_NAME = "Android Studio"
private const val CATEGORY_2_NAME = "Build"
private const val CATEGORY_3_NAME = "Compose"
private const val CATEGORY_SHORT_DESC = "At vero eos et accusamus."
private const val CATEGORY_LONG_DESC = "At vero eos et accusamus et iusto odio dignissimos ducimus."
private const val CATEGORY_URL = "URL"
private const val CATEGORY_IMAGE_URL = "Image URL"

private val testInputCategories = listOf(
    FollowableCategory2(
        Category(
            id = 0,
            name = CATEGORY_1_NAME,
            shortDescription = CATEGORY_SHORT_DESC,
            longDescription = CATEGORY_LONG_DESC,
            url = CATEGORY_URL,
            imageUrl = CATEGORY_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableCategory2(
        Category(
            id = 1,
            name = CATEGORY_2_NAME,
            shortDescription = CATEGORY_SHORT_DESC,
            longDescription = CATEGORY_LONG_DESC,
            url = CATEGORY_URL,
            imageUrl = CATEGORY_IMAGE_URL,
        ),
        isFollowed = false,
    ),
    FollowableCategory2(
        Category(
            id = 2,
            name = CATEGORY_3_NAME,
            shortDescription = CATEGORY_SHORT_DESC,
            longDescription = CATEGORY_LONG_DESC,
            url = CATEGORY_URL,
            imageUrl = CATEGORY_IMAGE_URL,
        ),
        isFollowed = false,
    ),
)

private val testOutputCategories = listOf(
    FollowableCategory2(
        Category(
            id = 0,
            name = CATEGORY_1_NAME,
            shortDescription = CATEGORY_SHORT_DESC,
            longDescription = CATEGORY_LONG_DESC,
            url = CATEGORY_URL,
            imageUrl = CATEGORY_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableCategory2(
        Category(
            id = 1,
            name = CATEGORY_2_NAME,
            shortDescription = CATEGORY_SHORT_DESC,
            longDescription = CATEGORY_LONG_DESC,
            url = CATEGORY_URL,
            imageUrl = CATEGORY_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableCategory2(
        Category(
            id = 2,
            name = CATEGORY_3_NAME,
            shortDescription = CATEGORY_SHORT_DESC,
            longDescription = CATEGORY_LONG_DESC,
            url = CATEGORY_URL,
            imageUrl = CATEGORY_IMAGE_URL,
        ),
        isFollowed = false,
    ),
)
