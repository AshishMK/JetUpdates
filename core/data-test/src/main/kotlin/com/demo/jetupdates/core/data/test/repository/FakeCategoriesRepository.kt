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

package com.demo.jetupdates.core.data.test.repository

import com.demo.jetupdates.core.data.Synchronizer
import com.demo.jetupdates.core.data.repository.CategoriesRepository
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.network.AppDispatchers.IO
import com.demo.jetupdates.core.network.Dispatcher
import com.demo.jetupdates.core.network.demo.DemoAppNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Fake implementation of the [CategoriesRepository] that retrieves the Categories from a JSON String, and
 * uses a local DataStore instance to save and retrieve followed category ids.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
internal class FakeCategoriesRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: DemoAppNetworkDataSource,
) : CategoriesRepository {
    override fun getCategories(): Flow<List<Category>> = flow {
        emit(
            datasource.getCategories().map {
                Category(
                    id = it.id,
                    name = it.name,
                    shortDescription = it.shortDescription,
                    longDescription = it.longDescription,
                    url = it.url,
                    imageUrl = it.imageUrl,
                )
            },
        )
    }.flowOn(ioDispatcher)

    override fun getCategory(id: Int): Flow<Category> = getCategories()
        .map { it.first { category -> category.id == id } }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
