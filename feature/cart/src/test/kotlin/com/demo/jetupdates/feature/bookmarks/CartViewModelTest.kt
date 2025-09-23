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

package com.demo.jetupdates.feature.bookmarks

import com.demo.jetupdates.core.data.repository.CompositeUserShopItemRepository
import com.demo.jetupdates.core.testing.data.shopItemsTestData
import com.demo.jetupdates.core.testing.repository.TestShopRepository
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.util.MainDispatcherRule
import com.demo.jetupdates.core.ui.ItemFeedUiState.Loading
import com.demo.jetupdates.core.ui.ItemFeedUiState.Success
import com.demo.jetupdates.feature.cart.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class CartViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val shopRepository = TestShopRepository()
    private val userShopItemRepository = CompositeUserShopItemRepository(
        shopRepository = shopRepository,
        userDataRepository = userDataRepository,
    )
    private lateinit var viewModel: CartViewModel

    @Before
    fun setup() {
        viewModel = CartViewModel(
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userShopItemRepository,
        )
    }

    @Test
    fun testDispatcher() {
        runTest(UnconfinedTestDispatcher()) {
            // Start a new coroutine
            val j = launch {
                // Do some work
                delay(10000)
                println("The first coroutine has completed")
            }
            // advanceTimeBy(100001)
            // Start another coroutine
            val j2 = launch {
                // Do some other work
                delay(100)
                println("The second coroutine has completed")
            }
            //  j.join()
            // j2.join()
            //  advanceUntilIdle()
            println("doneeee")
        }
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.feedUiState.value)
    }

    @Test
    fun oneItem_showsInCart() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }

        shopRepository.sendShopItems(shopItemsTestData)
        userDataRepository.setShopItemBookmarked(shopItemsTestData[0].id, true)
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(item.feed.size, 1)
    }

    @Test
    fun oneItem_whenRemoving_removesFromCart() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }
        // Set the news resources to be used by this test
        shopRepository.sendShopItems(shopItemsTestData)
        // Start with the resource saved
        userDataRepository.setShopItemBookmarked(shopItemsTestData[0].id, true)
        // Use viewModel to remove saved resource
        viewModel.removeFromSavedResources(shopItemsTestData[0].id)
        // Verify list of saved resources is now empty
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(item.feed.size, 0)
        assertTrue(viewModel.shouldDisplayUndoItem)
    }

    @Test
    fun feedUiState_itemIsViewed_setItemViewed() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }

        // Given
        shopRepository.sendShopItems(shopItemsTestData)
        userDataRepository.setShopItemBookmarked(shopItemsTestData[0].id, true)
        val itemBeforeViewed = viewModel.feedUiState.value
        assertIs<Success>(itemBeforeViewed)
        assertFalse(itemBeforeViewed.feed.first().hasBeenViewed)

        // When
        viewModel.setShopItemViewed(shopItemsTestData[0].id, true)

        // Then
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertTrue(item.feed.first().hasBeenViewed)
    }

    @Test
    fun feedUiState_undoneItemRemoval_itemIsRestored() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }

        // Given
        shopRepository.sendShopItems(shopItemsTestData)
        userDataRepository.setShopItemBookmarked(shopItemsTestData[0].id, true)
        viewModel.removeFromSavedResources(shopItemsTestData[0].id)
        assertTrue(viewModel.shouldDisplayUndoItem)
        val itemBeforeUndo = viewModel.feedUiState.value
        assertIs<Success>(itemBeforeUndo)
        assertEquals(0, itemBeforeUndo.feed.size)

        // When
        viewModel.undoItemRemoval()

        // Then
        assertFalse(viewModel.shouldDisplayUndoItem)
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(1, item.feed.size)
    }
}
