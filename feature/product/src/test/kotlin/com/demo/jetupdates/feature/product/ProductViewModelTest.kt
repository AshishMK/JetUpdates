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

package com.demo.jetupdates.feature.product

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.demo.jetupdates.core.data.repository.CompositeUserShopItemRepository
import com.demo.jetupdates.core.model.data.ShopItem
import com.demo.jetupdates.core.testing.data.categoriesTestData
import com.demo.jetupdates.core.testing.data.userShopItemsTestData
import com.demo.jetupdates.core.testing.repository.TestShopRepository
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.util.MainDispatcherRule
import com.demo.jetupdates.feature.product.navigation.ProductRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ProductViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val shopRepository = TestShopRepository()

    private val userShopItemRepository = CompositeUserShopItemRepository(
        shopRepository = shopRepository,
        userDataRepository = userDataRepository,
    )
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setup() {
        viewModel = ProductViewModel(
            userDataRepository = userDataRepository,
            userShopItemRepository = userShopItemRepository,
            savedStateHandle = SavedStateHandle(route = ProductRoute(initialProductId = userShopItemsTestData[0].id)),
        )
    }

    var shopItemId: Int = userShopItemsTestData[0].id
    val sample = ShopItem(
        id = 1,
        title = "Android Basics with Compose",
        description = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
            "\n" +
            "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. You’ll learn the fundamentals of programming in Kotlin while building Android apps using Jetpack Compose, Android’s modern toolkit that simplifies and accelerates native UI development. These two units are just the beginning; more will be coming soon. Check out Android Basics with Compose to get started on your Android development journey",
        price = 12f,
        images = listOf(
            "https://developer.android.com/images/hero-assets/android-basics-compose.svg",
            "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        ),
        stock = 12,
        publishDate = LocalDateTime(
            year = 2022,
            monthNumber = 5,
            dayOfMonth = 4,
            hour = 23,
            minute = 0,
            second = 0,
            nanosecond = 0,
        ).toInstant(TimeZone.UTC),
        type = "Codelab",
        categories = listOf(categoriesTestData[2]),
    )

    @Test
    fun uiStateProduct_whenSuccess_matchesProductFromRepository() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.productUiState.collect() }
        /*Because of combine operator in observeItem of CompositeUserShopItemRepository
        we have to make change in below both repos to be collected in above line*/
        shopRepository.sendShopItems(listOf(sample))
        // userDataRepository.setFollowedCategoryIds(setOf(2))
        userDataRepository.setShopItemBookmarked(shopItemId, true)
        val item = viewModel.productUiState.value
        assertIs<ProductUiState.Success>(item)

        val productFromRepository = userShopItemRepository.observeItem(
            shopItemId,
        ).first().id

        assertEquals(productFromRepository, item.product.id)
        assertTrue(item.product.isSaved)
    }

    @Test
    fun whenToggleShopItemSavedIsCalled_bookmarkStateIsUpdated() = runTest {
        viewModel.bookmarkItem(true)
        assertEquals(
            expected = setOf(shopItemId),
            actual = userDataRepository.userData.first().bookmarkedShopItems,
        )

        viewModel.bookmarkItem(false)

        assertEquals(
            expected = emptySet(), // no item iin user saved preference as we removed it from above line
            actual = userDataRepository.userData.first().bookmarkedShopItems,
        )
    }
}
