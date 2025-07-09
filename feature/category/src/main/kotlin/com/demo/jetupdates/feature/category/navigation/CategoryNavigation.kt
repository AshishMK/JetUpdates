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

package com.demo.jetupdates.feature.category.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.demo.jetupdates.feature.category.CategoryScreen
import com.demo.jetupdates.feature.category.CategoryViewModel
import kotlinx.serialization.Serializable

@Serializable
data class CategoryRoute(val id: Int)

fun NavController.navigateToCategory(
    categoryId: Int,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = CategoryRoute(categoryId)) {
        navOptions()
    }
}

fun NavGraphBuilder.categoryScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
) {
    composable<CategoryRoute> { entry ->
        val id = entry.toRoute<CategoryRoute>().id
        CategoryScreen(
            showBackButton = showBackButton,
            onBackClick = onBackClick,
            onProductClick = onCategoryClick,
            viewModel = hiltViewModel<CategoryViewModel, CategoryViewModel.Factory>(
                key = id.toString(),
            ) { factory ->
                factory.create(id)
            },
        )
    }
}
