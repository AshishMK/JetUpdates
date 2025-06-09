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

package com.demo.jetupdates.core.data.repository

import com.demo.jetupdates.core.data.Syncable
import com.demo.jetupdates.core.model.data.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository : Syncable {
    /**
     * Gets the available categories as a stream
     */
    fun getCategories(): Flow<List<Category>>

    /**
     * Gets data for a specific category
     */
    fun getCategory(id: Int): Flow<Category>
}
