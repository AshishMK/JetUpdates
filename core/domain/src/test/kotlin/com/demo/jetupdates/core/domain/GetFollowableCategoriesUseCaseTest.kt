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

package com.demo.jetupdates.core.domain

import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.testing.repository.TestCategoriesRepository
import com.demo.jetupdates.core.testing.repository.TestUserDataRepository
import com.demo.jetupdates.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetFollowableCategoriesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val categoriesRepository = TestCategoriesRepository()

    private val userDataRepository = TestUserDataRepository()

    val useCase = GetFollowableCategoriesUseCase(
        categoriesRepository,
        userDataRepository,
    )

    @Test
    fun whenNoParams_followableCategoriesAreReturnedWithNoSorting() = runTest {
        // Obtain a stream of followable categories.
        val followableCategories = useCase()

        // Send some test categories and their followed state.
        categoriesRepository.sendCategories(testCategories)
        userDataRepository.setFollowedCategoryIds(
            setOf(
                testCategories[0].id,
                testCategories[2].id,
            ),
        )

        // Check that the order hasn't changed and that the correct categories are marked as followed.
        assertEquals(
            listOf(
                FollowableCategory2(
                    testCategories[0],
                    true,
                ),
                FollowableCategory2(
                    testCategories[1],
                    false,
                ),
                FollowableCategory2(
                    testCategories[2],
                    true,
                ),
            ),
            followableCategories.first(),
        )
    }

    @Test
    fun whenSortOrderIsByName_categoriesSortedByNameAreReturned() = runTest {
        // Obtain a stream of followable categories, sorted by name.
        val followableCategories = useCase(
            CategorySortField.NAME,
        )

        // Send some test categories and their followed state.
        categoriesRepository.sendCategories(testCategories)
        userDataRepository.setFollowedCategoryIds(setOf())

        // Check that the followable categories are sorted by the category name.
        assertEquals(
            followableCategories.first(),
            testCategories
                .sortedBy { it.name }
                .map {
                    FollowableCategory2(it, false)
                },
        )
    }
}

private val testCategories = listOf(
    Category(1, "Headlines", "", "", "", ""),
    Category(2, "Android Studio", "", "", "", ""),
    Category(3, "Compose", "", "", "", ""),
)
