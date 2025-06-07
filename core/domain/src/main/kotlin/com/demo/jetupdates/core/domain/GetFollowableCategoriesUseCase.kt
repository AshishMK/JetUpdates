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

package com.demo.jetupdates.core.domain

import com.demo.jetupdates.core.data.repository.CategoriesRepository
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.domain.CategorySortField.NAME
import com.demo.jetupdates.core.domain.CategorySortField.NONE
import com.demo.jetupdates.core.model.data.FollowableCategory2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which obtains a list of categories with their followed state.
 */
class GetFollowableCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val userDataRepository: UserDataRepository,
) {
    /**
     * Returns a list of categories with their associated followed state.
     *
     * @param sortBy - the field used to sort the categories. Default NONE = no sorting.
     */
    operator fun invoke(sortBy: CategorySortField = NONE): Flow<List<FollowableCategory2>> = combine(
        userDataRepository.userData,
        categoriesRepository.getCategories(),
    ) { userData, categories ->
        val followedCategories = categories
            .map { category ->
                FollowableCategory2(
                    category = category,
                    isFollowed = category.id in userData.followedCategories,
                )
            }
        when (sortBy) {
            NAME -> followedCategories.sortedBy { it.category.name }
            else -> followedCategories
        }
    }
}

enum class CategorySortField {
    NONE,
    NAME,
}
