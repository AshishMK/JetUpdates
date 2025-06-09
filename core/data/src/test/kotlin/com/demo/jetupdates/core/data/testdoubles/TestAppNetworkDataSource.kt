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

package com.demo.jetupdates.core.data.testdoubles

import com.demo.jetupdates.core.network.AppNetworkDataSource
import com.demo.jetupdates.core.network.demo.DemoAppNetworkDataSource
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.serialization.json.Json

enum class CollectionType {
    Categories,
    ShopItems,
}

/**
 * Test double for [AppNetworkDataSource]
 */
class TestAppNetworkDataSource : AppNetworkDataSource {

    private val source = DemoAppNetworkDataSource(
        UnconfinedTestDispatcher(),
        Json { ignoreUnknownKeys = true },
    )

    private val allCategories = runBlocking { source.getCategories() }

    private val allShopItems = runBlocking { source.getShopItems() }

    private val changeLists: MutableMap<CollectionType, List<NetworkChangeList>> = mutableMapOf(
        CollectionType.Categories to allCategories
            .mapToChangeList(idGetter = NetworkCategory::id),
        CollectionType.ShopItems to allShopItems
            .mapToChangeList(idGetter = NetworkShopItem::id),
    )

    override suspend fun getCategories(ids: List<Int>?): List<NetworkCategory> =
        allCategories.matchIds(
            ids = ids,
            idGetter = NetworkCategory::id,
        )

    override suspend fun getShopItems(ids: List<Int>?): List<NetworkShopItem> =
        allShopItems.matchIds(
            ids = ids,
            idGetter = NetworkShopItem::id,
        )

    override suspend fun getCategoryChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(CollectionType.Categories).after(after)

    override suspend fun getShopItemChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(CollectionType.ShopItems).after(after)

    fun latestChangeListVersion(collectionType: CollectionType) =
        changeLists.getValue(collectionType).last().changeListVersion

    fun changeListsAfter(collectionType: CollectionType, version: Int) =
        changeLists.getValue(collectionType).after(version)

    /**
     * Edits the change list for the backing [collectionType] for the given [id] mimicking
     * the server's change list registry
     */
    fun editCollection(collectionType: CollectionType, id: Int, isDelete: Boolean) {
        val changeList = changeLists.getValue(collectionType)
        val latestVersion = changeList.lastOrNull()?.changeListVersion ?: 0
        val change = NetworkChangeList(
            id = id,
            isDelete = isDelete,
            changeListVersion = latestVersion + 1,
        )
        changeLists[collectionType] = changeList.filterNot { it.id == id } + change
    }
}

fun List<NetworkChangeList>.after(version: Int?): List<NetworkChangeList> = when (version) {
    null -> this
    else -> filter { it.changeListVersion > version }
}

/**
 * Return items from [this] whose id defined by [idGetter] is in [ids] if [ids] is not null
 */
private fun <T> List<T>.matchIds(
    ids: List<Int>?,
    idGetter: (T) -> Int,
) = when (ids) {
    null -> this
    else -> ids.toSet().let { idSet -> filter { idGetter(it) in idSet } }
}

/**
 * Maps items to a change list where the change list version is denoted by the index of each item.
 * [after] simulates which models have changed by excluding items before it
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> Int,
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index + 1,
        isDelete = false,
    )
}
