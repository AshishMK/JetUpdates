/*
 * Copyright 2023 The Android Open Source Project
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

package com.demo.jetupdates.core.model.data

import kotlinx.datetime.Instant

/**
 * A [ShopItem] with additional user information such as whether the user is following the
 * shop item's categories and whether they have saved (bookmarked) this shop item.
 */
data class UserShopItem internal constructor(
    val id: Int,
    val title: String,
    val price: Float,
    val description: String,
    val stock: Int,
    val images: List<String>,
    val publishDate: Instant,
    val type: String,
    val followableCategories: List<FollowableCategory2>,
    val isSaved: Boolean,
    val hasBeenViewed: Boolean,
) {
    constructor(shopItem: ShopItem, userData: UserData) : this(
        id = shopItem.id,
        title = shopItem.title,
        price = shopItem.price,
        description = shopItem.description,
        stock = shopItem.stock,
        images = shopItem.images,
        publishDate = shopItem.publishDate,
        type = shopItem.type,
        followableCategories = shopItem.categories.map { category ->
            FollowableCategory2(
                category = category,
                isFollowed = category.id in userData.followedCategories,
            )
        },
        isSaved = shopItem.id in userData.bookmarkedShopItems,
        hasBeenViewed = shopItem.id in userData.viewedShopItems,
    )
}

fun List<ShopItem>.mapToUserShopItems(userData: UserData): List<UserShopItem> =
    map { UserShopItem(it, userData) }
