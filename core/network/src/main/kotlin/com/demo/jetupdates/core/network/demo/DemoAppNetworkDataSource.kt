/*
 * Copyright 2024 The Android Open Source Project
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

package com.demo.jetupdates.core.network.demo

import JvmUnitTestDemoAssetManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import com.demo.jetupdates.core.network.AppDispatchers.IO
import com.demo.jetupdates.core.network.AppNetworkDataSource
import com.demo.jetupdates.core.network.Dispatcher
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.BufferedReader
import javax.inject.Inject

/**
 * [AppNetworkDataSource] implementation that provides static shop items to aid development
 */
class DemoAppNetworkDataSource @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: DemoAssetManager = JvmUnitTestDemoAssetManager,
) : AppNetworkDataSource {

    override suspend fun getCategories(ids: List<Int>?): List<NetworkCategory> =
        getDataFromJsonFile(CATEGORIES_ASSET)

    override suspend fun getShopItems(ids: List<Int>?): List<NetworkShopItem> =
        getDataFromJsonFile(ITEMS_ASSET)

    override suspend fun getCategoryChangeList(after: Int?): List<NetworkChangeList> =
        getCategories().mapToChangeList(NetworkCategory::id)

    override suspend fun getShopItemChangeList(after: Int?): List<NetworkChangeList> =
        getShopItems().mapToChangeList(NetworkShopItem::id)

    /**
     * Get data from the given JSON [fileName].
     */
    @OptIn(ExperimentalSerializationApi::class)
    private suspend inline fun <reified T> getDataFromJsonFile(fileName: String): List<T> =
        withContext(ioDispatcher) {
            assets.open(fileName).use { inputStream ->

                if (SDK_INT <= M) {
                    /**
                     * On API 23 (M) and below we must use a workaround to avoid an exception being
                     * thrown during deserialization. See:
                     * https://github.com/Kotlin/kotlinx.serialization/issues/2457#issuecomment-1786923342
                     */
                    inputStream.bufferedReader().use(BufferedReader::readText)
                        .let(networkJson::decodeFromString)
                } else {
// you can put it under try catch block if there is an error

                    networkJson.decodeFromStream(inputStream) as List<T>
                }
            }
        }

    companion object {
        private const val ITEMS_ASSET = "items.json"
        private const val CATEGORIES_ASSET = "categories.json"
    }
}

/**
 * Converts a list of [T] to change list of all the items in it where [idGetter] defines the
 * [NetworkChangeList.id]
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> Int,
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index,
        isDelete = false,
    )
}
