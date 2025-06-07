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

package com.demo.jetupdates.core.testing.repository

import com.demo.jetupdates.core.data.Synchronizer
import com.demo.jetupdates.core.data.repository.CategoriesRepository
import com.demo.jetupdates.core.model.data.Category
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestCategoriesRepository : CategoriesRepository {
    /**
     * The backing hot flow for the list of categories ids for testing.
     */
    private val categoriesFlow: MutableSharedFlow<List<Category>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getCategories(): Flow<List<Category>> = categoriesFlow

    override fun getCategory(id: Int): Flow<Category> =
        categoriesFlow.map { categories -> categories.find { it.id == id }!! }

    /**
     * A test-only API to allow controlling the list of categories from tests.
     */
    fun sendCategories(categories: List<Category>) {
        categoriesFlow.tryEmit(categories)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
