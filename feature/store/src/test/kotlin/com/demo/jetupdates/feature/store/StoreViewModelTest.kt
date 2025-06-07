/*
 * Copyright 2025 The Android Open Source Project
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

package com.demo.jetupdates.feature.store

import androidx.lifecycle.SavedStateHandle
import com.demo.jetupdates.core.data.repository.CompositeUserShopItemRepository
import com.demo.jetupdates.core.domain.GetFollowableCategoriesUseCase
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.model.data.mapToUserShopItems
import com.demo.jetupdates.core.notifications.DEEP_LINK_SHOP_ITEM_ID_KEY
import com.demo.jetupdates.core.testing.repository.TestCategoriesRepository
import com.demo.jetupdates.core.testing.repository.TestShopRepository
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.repository.emptyUserData
import com.demo.jetupdates.core.testing.util.MainDispatcherRule
import com.demo.jetupdates.core.testing.util.TestSyncManager
import com.demo.jetupdates.core.ui.ItemFeedUiState
import com.demo.jetupdates.core.ui.ItemFeedUiState.Success
import com.demo.jetupdates.feature.store.OnboardingUiState.NotShown
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class StoreViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()

    private val userDataRepository = TestUserDataRepository()
    private val categoriesRepository =
        TestCategoriesRepository()
    private val shopRepository =
        TestShopRepository()
    private val userShopItemRepository =
        CompositeUserShopItemRepository(
            shopRepository = shopRepository,
            userDataRepository = userDataRepository,
        )

    private val getFollowableCategoriesUseCase =
        GetFollowableCategoriesUseCase(
            categoriesRepository = categoriesRepository,
            userDataRepository = userDataRepository,
        )

    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: StoreViewModel

    @Before
    fun setup() {
        viewModel = StoreViewModel(
            syncManager = syncManager,
            savedStateHandle = savedStateHandle,
            userDataRepository = userDataRepository,
            userShopItemRepository = userShopItemRepository,
            getFollowableCategories = getFollowableCategoriesUseCase,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(
            OnboardingUiState.Loading,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Loading,
            viewModel.feedState.value,
        )
    }

    @Test
    fun stateIsLoadingWhenFollowedCategoriesAreLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        categoriesRepository.sendCategories(sampleCategories)

        assertEquals(
            OnboardingUiState.Loading,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Loading,
            viewModel.feedState.value,
        )
    }

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoInterests() = runTest {
        syncManager.setSyncing(true)

        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            true,
            viewModel.isSyncing.value,
        )
    }

    @Test
    fun onboardingStateIsLoadingWhenCategoriesAreLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        userDataRepository.setFollowedCategoryIds(emptySet())

        assertEquals(
            OnboardingUiState.Loading,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Success(emptyList()),
            viewModel.feedState.value,
        )
    }

    @Test
    fun onboardingIsShownWhenShopItemsAreLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        categoriesRepository.sendCategories(sampleCategories)
        userDataRepository.setFollowedCategoryIds(emptySet())

        assertEquals(
            OnboardingUiState.Shown(
                categories = listOf(
                    FollowableCategory2(
                        category = Category(
                            id = 0,
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 1,
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 2,
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
                shouldShowOnboarding = true,
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun onboardingIsShownAfterLoadingEmptyFollowedCategories() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        categoriesRepository.sendCategories(sampleCategories)
        userDataRepository.setFollowedCategoryIds(emptySet())
        shopRepository.sendShopItems(sampleShopItems)

        assertEquals(
            OnboardingUiState.Shown(
                categories = listOf(
                    FollowableCategory2(
                        category = Category(
                            id = 0,
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 1,
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 2,
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
                shouldShowOnboarding = true,
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun onboardingIsNotShownAfterUserDismissesOnboarding() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        categoriesRepository.sendCategories(sampleCategories)

        val followedCategoryIds = setOf(0, 1)
        val userData =
            emptyUserData.copy(
                followedCategories = followedCategoryIds,
            )
        userDataRepository.setUserData(userData)
        viewModel.dismissOnboarding()

        assertEquals(
            NotShown,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Loading,
            viewModel.feedState.value,
        )

        shopRepository.sendShopItems(sampleShopItems)

        assertEquals(
            OnboardingUiState.NotShown,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Success(
                feed = sampleShopItems.mapToUserShopItems(
                    userData,
                ),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun categorySelectionUpdatesAfterSelectingCategory() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        categoriesRepository.sendCategories(sampleCategories)
        userDataRepository.setFollowedCategoryIds(emptySet())
        shopRepository.sendShopItems(sampleShopItems)

        assertEquals(
            OnboardingUiState.Shown(
                sampleCategories.map {
                    FollowableCategory2(it, false)
                },
                shouldShowOnboarding = true,
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.feedState.value,
        )

        val followedCategoryId = sampleCategories[1].id
        viewModel.updateCategorySelection(followedCategoryId, isChecked = true)

        assertEquals(
            OnboardingUiState.Shown(
                categories = sampleCategories.map {
                    FollowableCategory2(
                        it,
                        it.id == followedCategoryId,
                    )
                },
                shouldShowOnboarding = true,
            ),
            viewModel.onboardingUiState.value,
        )

        val userData =
            emptyUserData.copy(
                followedCategories = setOf(followedCategoryId),
            )

        assertEquals(
            ItemFeedUiState.Success(
                feed = listOf(
                    UserShopItem(
                        sampleShopItems[1],
                        userData,
                    ),
                    UserShopItem(
                        sampleShopItems[2],
                        userData,
                    ),
                ),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun categorySelectionUpdatesAfterUnselectingCategory() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        categoriesRepository.sendCategories(sampleCategories)
        userDataRepository.setFollowedCategoryIds(emptySet())
        shopRepository.sendShopItems(sampleShopItems)
        viewModel.updateCategorySelection(1, isChecked = true)
        viewModel.updateCategorySelection(1, isChecked = false)

        advanceUntilIdle()
        assertEquals(
            OnboardingUiState.Shown(
                categories = listOf(
                    FollowableCategory2(
                        category = Category(
                            id = 0,
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 1,
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 2,
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
                shouldShowOnboarding = true,
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            ItemFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun shopItemSelectionUpdatesAfterLoadingFollowedCategories() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        val followedCategoryIds = setOf(1)
        val userData =
            emptyUserData.copy(
                followedCategories = followedCategoryIds,
                shouldHideOnboarding = true,
            )

        categoriesRepository.sendCategories(sampleCategories)
        userDataRepository.setUserData(userData)
        shopRepository.sendShopItems(sampleShopItems)

        val bookmarkedShopItemId = 2
        viewModel.updateShopItemSaved(
            shopItemId = bookmarkedShopItemId,
            isChecked = true,
        )

        val userDataExpected = userData.copy(
            bookmarkedShopItems = setOf(bookmarkedShopItemId),
        )

        assertEquals(
            OnboardingUiState.NotShown,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            Success(
                feed = listOf(
                    UserShopItem(
                        shopItem = sampleShopItems[1],
                        userDataExpected,
                    ),
                    UserShopItem(
                        shopItem = sampleShopItems[2],
                        userDataExpected,
                    ),
                ),
            ),
            viewModel.feedState.value,
        )
    }

    @Test
    fun deepLinkedShopItemIsFetchedAndResetAfterViewing() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.deepLinkedShopItem.collect() }

        shopRepository.sendShopItems(sampleShopItems)
        userDataRepository.setUserData(emptyUserData)
        savedStateHandle[DEEP_LINK_SHOP_ITEM_ID_KEY] =
            sampleShopItems.first().id

        assertEquals(
            expected = UserShopItem(
                shopItem = sampleShopItems.first(),
                userData = emptyUserData,
            ),
            actual = viewModel.deepLinkedShopItem.value,
        )

        viewModel.onDeepLinkOpened(
            shopItemId = sampleShopItems.first().id,
        )

        assertNull(
            viewModel.deepLinkedShopItem.value,
        )
    }

    @Test
    fun whenUpdateShopItemSavedIsCalled_bookmarkStateIsUpdated() = runTest {
        val shopItemId = 123
        viewModel.updateShopItemSaved(shopItemId, true)

        assertEquals(
            expected = setOf(shopItemId),
            actual = userDataRepository.userData.first().bookmarkedShopItems,
        )

        viewModel.updateShopItemSaved(shopItemId, false)

        assertEquals(
            expected = emptySet(),
            actual = userDataRepository.userData.first().bookmarkedShopItems,
        )
    }

    @Test
    fun showCategoriesAndFollowedShopItemsAfterHideOnboarding() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        val followedCategoryIds = setOf(1)
        val userData = emptyUserData.copy(
            followedCategories = followedCategoryIds,
            shouldHideOnboarding = true,
        )

        viewModel.categoryActionClicked(true)

        categoriesRepository.sendCategories(sampleCategories)
        userDataRepository.setUserData(userData)
        shopRepository.sendShopItems(sampleShopItems)

        assertEquals(
            OnboardingUiState.Shown(
                categories = listOf(
                    FollowableCategory2(
                        category = Category(
                            id = 0,
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 1,
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = true,
                    ),
                    FollowableCategory2(
                        category = Category(
                            id = 2,
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
                shouldShowOnboarding = false,
            ),
            viewModel.onboardingUiState.value,
        )

        assertEquals(
            ItemFeedUiState.Success(
                feed = listOf(
                    UserShopItem(shopItem = sampleShopItems[1], userData),
                    UserShopItem(shopItem = sampleShopItems[2], userData),
                ),
            ),
            viewModel.feedState.value,
        )
    }
}

private val sampleCategories = listOf(
    Category(
        id = 0,
        name = "Headlines",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Category(
        id = 1,
        name = "UI",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Category(
        id = 2,
        name = "Tools",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
)

private val sampleShopItems = listOf(
    ShopItem(
        id = 1,
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        price = 11.45f,
        description = "Thank you everyone for following the JU App series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        stock = 345,
        images = listOf(
            "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
            "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        ),
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
    ShopItem(
        id = 2,
        title = "Transformations and customisations in the Paging Library",
        price = 123.00f,
        description = "A demonstration of different operations that can be performed with Paging. " +
            "Transformations like inserting separators, when to create a new pager, and " +
            "customisation options for consuming PagingData.",
        stock = 85,
        images = listOf(
            "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
            "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        ),
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(
            Category(
                id = 1,
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    ShopItem(
        id = 3,
        title = "Community tip on Paging",
        price = 180f,
        description = "Tips for using the Paging library from the developer community",
        stock = 156,
        images = listOf(
            "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
            "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        ),
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = "Video 📺",
        categories = listOf(
            Category(
                id = 1,
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
)
