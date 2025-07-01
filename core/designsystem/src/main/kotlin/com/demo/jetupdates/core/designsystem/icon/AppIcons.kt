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

package com.demo.jetupdates.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.BubbleChart
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.RemoveShoppingCart
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * JU App icons. Material icons are [ImageVector]s, custom icons are drawable resource IDs.
 */
object AppIcons {
    val Add = Icons.Rounded.Add
    val FavoriteBorder = Icons.Rounded.FavoriteBorder
    val Favorite = Icons.Rounded.Favorite
    val ArrowBack = Icons.AutoMirrored.Rounded.ArrowBack
    val Bookmark = Icons.Rounded.Bookmark
    val BookmarkBorder = Icons.Rounded.BookmarkBorder
    val Cart = Icons.Rounded.ShoppingCart
    val CartBorder = Icons.Outlined.ShoppingCart
    val Check = Icons.Rounded.Check
    val Close = Icons.Rounded.Close
    val Fire = Icons.Rounded.LocalFireDepartment
    val FireBorder = Icons.Outlined.LocalFireDepartment

    val Chat = Icons.Rounded.BubbleChart
    val ChatBorder = Icons.Outlined.BubbleChart
    val MoreVert = Icons.Default.MoreVert
    val Person = Icons.Rounded.Person
    val Search = Icons.Rounded.Search
    val Settings = Icons.Rounded.Settings
    val ShortText = Icons.AutoMirrored.Rounded.ShortText
    val Store = Icons.Rounded.Store
    val StoreBorder = Icons.Outlined.Store
    val ViewDay = Icons.Rounded.ViewDay

    val AddToCart = Icons.Rounded.AddShoppingCart
    val RemoveFromCart = Icons.Rounded.RemoveShoppingCart

    val Category = Icons.Rounded.Category

    val Emoji = Icons.Outlined.Mood

    val Send = Icons.AutoMirrored.Rounded.Send
    val arrowDown = Icons.Filled.ArrowDownward
}
