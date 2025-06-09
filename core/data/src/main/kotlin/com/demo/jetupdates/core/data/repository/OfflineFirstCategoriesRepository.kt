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

import com.demo.jetupdates.core.data.Synchronizer
import com.demo.jetupdates.core.data.changeListSync
import com.demo.jetupdates.core.data.model.asEntity
import com.demo.jetupdates.core.database.dao.CategoryDao
import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.asExternalModel
import com.demo.jetupdates.core.datastore.ChangeListVersions
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.network.AppNetworkDataSource
import com.demo.jetupdates.core.network.model.NetworkCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [CategoriesRepository].
 * Reads are exclusively from local storage to support offline access.
 */
internal class OfflineFirstCategoriesRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val network: AppNetworkDataSource,
) : CategoriesRepository {

    override fun getCategories(): Flow<List<Category>> =
        categoryDao.getCategoryEntities()
            .map { it.map(CategoryEntity::asExternalModel) }

    override fun getCategory(id: Int): Flow<Category> =
        categoryDao.getCategoryEntity(id).map { it.asExternalModel() }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
            versionReader = ChangeListVersions::categoryVersion,
            changeListFetcher = { currentVersion ->
                network.getCategoryChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(categoryVersion = latestVersion)
            },
            modelDeleter = categoryDao::deleteCategories,
            modelUpdater = { changedIds ->
                val networkCategories = network.getCategories(ids = changedIds)
                categoryDao.upsertCategories(
                    entities = networkCategories.map(NetworkCategory::asEntity),
                )
            },
        )
}
