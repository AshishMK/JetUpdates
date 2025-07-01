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

package com.demo.jetupdates.feature.category

import com.demo.jetupdates.core.data.repository.CompositeUserShopItemRepository
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.testing.repository.TestCategoriesRepository
import com.demo.jetupdates.core.testing.repository.TestShopRepository
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class CategoryViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val categoriesRepository = TestCategoriesRepository()
    private val shopRepository = TestShopRepository()
    private val userShopItemRepository = CompositeUserShopItemRepository(
        shopRepository = shopRepository,
        userDataRepository = userDataRepository,
    )
    private lateinit var viewModel: CategoryViewModel

    @Before
    fun setup() {
        viewModel = CategoryViewModel(
            userDataRepository = userDataRepository,
            categoriesRepository = categoriesRepository,
            userShopItemRepository = userShopItemRepository,
            categoryId = testInputCategories[0].category.id,
        )
    }

    @Test
    fun categoryId_matchesCategoryIdFromSavedStateHandle() =
        assertEquals(testInputCategories[0].category.id, viewModel.categoryId)

    @Test
    fun uiStateCategory_whenSuccess_matchesCategoryFromRepository() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.categoryUiState.collect() }

        categoriesRepository.sendCategories(testInputCategories.map(FollowableCategory2::category))
        userDataRepository.setFollowedCategoryIds(setOf(testInputCategories[1].category.id))
        val item = viewModel.categoryUiState.value
        assertIs<CategoryUiState.Success>(item)

        val categoryFromRepository = categoriesRepository.getCategory(
            testInputCategories[0].category.id,
        ).first()

        assertEquals(categoryFromRepository, item.followableCategory.category)
    }

    @Test
    fun uiStateNews_whenInitialized_thenShowLoading() = runTest {
        assertEquals(ShopItemUiState.Loading, viewModel.shopItemUiState.value)
    }

    @Test
    fun uiStateCategory_whenInitialized_thenShowLoading() = runTest {
        assertEquals(CategoryUiState.Loading, viewModel.categoryUiState.value)
    }

    @Test
    fun uiStateCategory_whenFollowedIdsSuccessAndCategoryLoading_thenShowLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.categoryUiState.collect() }

        userDataRepository.setFollowedCategoryIds(setOf(testInputCategories[1].category.id))
        assertEquals(CategoryUiState.Loading, viewModel.categoryUiState.value)
    }

    @Test
    fun uiStateCategory_whenFollowedIdsSuccessAndCategorySuccess_thenCategorySuccessAndNewsLoading() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.categoryUiState.collect() }

            categoriesRepository.sendCategories(testInputCategories.map { it.category })
            userDataRepository.setFollowedCategoryIds(setOf(testInputCategories[1].category.id))
            val categoryUiState = viewModel.categoryUiState.value
            val shopUiState = viewModel.shopItemUiState.value

            assertIs<CategoryUiState.Success>(categoryUiState)
            assertIs<ShopItemUiState.Loading>(shopUiState)
        }

    @Test
    fun uiStateCategory_whenFollowedIdsSuccessAndCategorySuccessAndNewsIsSuccess_thenAllSuccess() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) {
                combine(
                    viewModel.categoryUiState,
                    viewModel.shopItemUiState,
                    ::Pair,
                ).collect()
            }
            categoriesRepository.sendCategories(testInputCategories.map { it.category })
            userDataRepository.setFollowedCategoryIds(setOf(testInputCategories[1].category.id))
            shopRepository.sendShopItems(sampleShopItems)
            val categoryUiState = viewModel.categoryUiState.value
            val shopUiState = viewModel.shopItemUiState.value

            assertIs<CategoryUiState.Success>(categoryUiState)
            assertIs<ShopItemUiState.Success>(shopUiState)
        }

    @Test
    fun uiStateCategory_whenFollowingCategory_thenShowUpdatedCategory() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.categoryUiState.collect() }

        categoriesRepository.sendCategories(testInputCategories.map { it.category })
        // Set which category IDs are followed, not including 0.
        userDataRepository.setFollowedCategoryIds(setOf(testInputCategories[1].category.id))

        viewModel.followCategoryToggle(true)

        assertEquals(
            CategoryUiState.Success(followableCategory = testOutputCategories[0]),
            viewModel.categoryUiState.value,
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

private val sampleShopItems = listOf(
    ShopItem(
        id = 1,
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        price = 329f,
        description = "Thank you everyone for following the Now in Android series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        stock = 178,
        images = listOf("https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg", "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg"),
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(
            Category(
                id = 0,
                name = "Headlines",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
)
