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

package com.demo.jetupdates.core.network.model

import com.demo.jetupdates.core.model.data.Category
import kotlinx.serialization.Serializable

/**
 * Network representation of [Category]
 */
@Serializable
data class NetworkCategory(
    val id: Int,
    val name: String = "",
    val shortDescription: String = "",
    val longDescription: String = "",
    val url: String = "",
    val imageUrl: String = "",
    val followed: Boolean = false,
)

fun NetworkCategory.asExternalModel(): Category =
    Category(
        id = id,
        name = name,
        shortDescription = shortDescription,
        longDescription = longDescription,
        url = url,
        imageUrl = imageUrl,
    )
