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

package com.demo.jetupdates.core.network

import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import com.demo.jetupdates.core.network.model.NetworkShopItem

/**
 * Interface representing network calls to the JU App backend
 */
interface AppNetworkDataSource {
    suspend fun getCategories(ids: List<Int>? = null): List<NetworkCategory>

    suspend fun getShopItems(ids: List<Int>? = null): List<NetworkShopItem>

    suspend fun getCategoryChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getShopItemChangeList(after: Int? = null): List<NetworkChangeList>
}
